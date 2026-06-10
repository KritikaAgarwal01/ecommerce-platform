using Microsoft.AspNetCore.Mvc;
using PaymentService.Models;
using PaymentService.Services;

namespace PaymentService.Controllers;

[ApiController]
[Route("api/payments")]
public class PaymentsController : ControllerBase
{
    private readonly IPaymentProcessor _paymentProcessor;
    private readonly PaymentDbContext _dbContext;

    public PaymentsController(IPaymentProcessor paymentProcessor, PaymentDbContext dbContext)
    {
        _paymentProcessor = paymentProcessor;
        _dbContext = dbContext;
    }

    [HttpPost]
    public async Task<ActionResult<PaymentResponse>> ProcessPayment([FromBody] PaymentRequest request)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var result = await _paymentProcessor.ProcessAsync(request);
        return CreatedAtAction(nameof(GetPayment), new { id = result.PaymentId }, result);
    }

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<Payment>> GetPayment(Guid id)
    {
        var payment = await _dbContext.Payments.FindAsync(id);
        if (payment is null)
            return NotFound();

        return Ok(payment);
    }

    [HttpGet("order/{orderId:guid}")]
    public async Task<ActionResult<IEnumerable<Payment>>> GetPaymentsByOrder(Guid orderId)
    {
        var payments = _dbContext.Payments
            .Where(p => p.OrderId == orderId)
            .OrderByDescending(p => p.CreatedAt);

        return Ok(await payments.ToListAsync());
    }

    [HttpPost("{id:guid}/refund")]
    public async Task<ActionResult<PaymentResponse>> RefundPayment(Guid id)
    {
        var payment = await _dbContext.Payments.FindAsync(id);
        if (payment is null)
            return NotFound();

        var result = await _paymentProcessor.RefundAsync(id);
        return Ok(result);
    }
}
