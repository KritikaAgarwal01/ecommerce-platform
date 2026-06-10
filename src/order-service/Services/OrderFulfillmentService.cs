using OrderService.Models;

namespace OrderService.Services;

public interface IOrderFulfillmentService
{
    Task<OrderResponse> CreateOrderAsync(CreateOrderRequest request);
    Task<OrderResponse?> UpdateStatusAsync(Guid orderId, OrderStatus status);
    Task<OrderResponse?> CancelOrderAsync(Guid orderId);
}

public class OrderFulfillmentService : IOrderFulfillmentService
{
    private readonly OrderDbContext _dbContext;
    private readonly IPaymentClient _paymentClient;
    private readonly ICartClient _cartClient;
    private readonly ILogger<OrderFulfillmentService> _logger;

    public OrderFulfillmentService(
        OrderDbContext dbContext,
        IPaymentClient paymentClient,
        ICartClient cartClient,
        ILogger<OrderFulfillmentService> logger)
    {
        _dbContext = dbContext;
        _paymentClient = paymentClient;
        _cartClient = cartClient;
        _logger = logger;
    }

    public async Task<OrderResponse> CreateOrderAsync(CreateOrderRequest request)
    {
        // Retrieve cart items for the user
        var cartItems = await _cartClient.GetCartItemsAsync(request.UserId);

        if (cartItems is null || !cartItems.Any())
        {
            return new OrderResponse
            {
                Status = OrderStatus.Cancelled,
                Message = "Cart is empty"
            };
        }

        var order = new Order
        {
            Id = Guid.NewGuid(),
            UserId = request.UserId,
            ShippingAddress = request.ShippingAddress,
            Status = OrderStatus.Pending,
            CreatedAt = DateTime.UtcNow,
            Items = cartItems.Select(item => new OrderItem
            {
                Id = Guid.NewGuid(),
                ProductId = item.ProductId,
                ProductName = item.ProductName,
                Quantity = item.Quantity,
                UnitPrice = item.UnitPrice
            }).ToList()
        };

        order.TotalAmount = order.Items.Sum(i => i.Quantity * i.UnitPrice);

        _dbContext.Orders.Add(order);
        await _dbContext.SaveChangesAsync();

        // Initiate payment
        order.Status = OrderStatus.PaymentProcessing;
        var paymentResult = await _paymentClient.ProcessPaymentAsync(
            order.Id, order.TotalAmount, request.PaymentMethod);

        if (paymentResult.Success)
        {
            order.Status = OrderStatus.Confirmed;
            order.PaymentId = paymentResult.PaymentId;

            // Clear the cart after successful order
            await _cartClient.ClearCartAsync(request.UserId);
        }
        else
        {
            order.Status = OrderStatus.Cancelled;
        }

        order.UpdatedAt = DateTime.UtcNow;
        await _dbContext.SaveChangesAsync();

        _logger.LogInformation("Order {OrderId} created with status {Status}", order.Id, order.Status);

        return new OrderResponse
        {
            OrderId = order.Id,
            Status = order.Status,
            TotalAmount = order.TotalAmount,
            Message = order.Status == OrderStatus.Confirmed
                ? "Order confirmed and payment processed"
                : "Order creation failed — payment was not successful"
        };
    }

    public async Task<OrderResponse?> UpdateStatusAsync(Guid orderId, OrderStatus status)
    {
        var order = await _dbContext.Orders.FindAsync(orderId);
        if (order is null) return null;

        order.Status = status;
        order.UpdatedAt = DateTime.UtcNow;

        if (status == OrderStatus.Completed)
            order.CompletedAt = DateTime.UtcNow;

        await _dbContext.SaveChangesAsync();

        return new OrderResponse
        {
            OrderId = order.Id,
            Status = order.Status,
            TotalAmount = order.TotalAmount,
            Message = $"Order status updated to {status}"
        };
    }

    public async Task<OrderResponse?> CancelOrderAsync(Guid orderId)
    {
        var order = await _dbContext.Orders.FindAsync(orderId);
        if (order is null) return null;

        if (order.Status is OrderStatus.Completed or OrderStatus.Delivered)
        {
            return new OrderResponse
            {
                OrderId = order.Id,
                Status = order.Status,
                TotalAmount = order.TotalAmount,
                Message = "Cannot cancel a completed or delivered order"
            };
        }

        order.Status = OrderStatus.Cancelled;
        order.UpdatedAt = DateTime.UtcNow;
        await _dbContext.SaveChangesAsync();

        // Trigger refund if payment was processed
        if (order.PaymentId.HasValue)
        {
            await _paymentClient.RefundPaymentAsync(order.PaymentId.Value);
        }

        return new OrderResponse
        {
            OrderId = order.Id,
            Status = OrderStatus.Cancelled,
            TotalAmount = order.TotalAmount,
            Message = "Order cancelled successfully"
        };
    }
}
