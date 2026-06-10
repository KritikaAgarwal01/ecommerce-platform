import { Router, Request, Response } from 'express';
import { body, param, validationResult } from 'express-validator';
import { CartService } from '../services/cartService';

const router = Router();
const cartService = new CartService();

router.get('/:userId', [
  param('userId').isString().notEmpty()
], async (req: Request, res: Response) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }

  const cart = await cartService.getCart(req.params.userId);
  if (!cart) {
    return res.status(404).json({ message: 'Cart not found' });
  }
  return res.json(cart);
});

router.post('/:userId/items', [
  param('userId').isString().notEmpty(),
  body('productId').isString().notEmpty(),
  body('productName').isString().notEmpty(),
  body('quantity').isInt({ min: 1 }),
  body('unitPrice').isFloat({ min: 0 })
], async (req: Request, res: Response) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }

  const cart = await cartService.addItem(req.params.userId, {
    productId: req.body.productId,
    productName: req.body.productName,
    quantity: req.body.quantity,
    unitPrice: req.body.unitPrice
  });
  return res.status(201).json(cart);
});

router.put('/:userId/items/:productId', [
  param('userId').isString().notEmpty(),
  param('productId').isString().notEmpty(),
  body('quantity').isInt({ min: 0 })
], async (req: Request, res: Response) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }

  const cart = await cartService.updateItemQuantity(
    req.params.userId,
    req.params.productId,
    req.body.quantity
  );
  if (!cart) {
    return res.status(404).json({ message: 'Cart or item not found' });
  }
  return res.json(cart);
});

router.delete('/:userId', [
  param('userId').isString().notEmpty()
], async (req: Request, res: Response) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }

  await cartService.clearCart(req.params.userId);
  return res.status(204).send();
});

export { router as cartRoutes };
