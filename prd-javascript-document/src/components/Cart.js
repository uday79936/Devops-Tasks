import React from "react";
import "../styles.css";

function Cart({ cart }) {
  const total = cart.reduce((sum, item) => sum + item.price, 0);

  return (
    <div className="cart-card">
      <h2 className="cart-title">🛒 Your Cart</h2>

      {cart.length === 0 ? (
        <p className="empty-cart">😔 No items in cart</p>
      ) : (
        <>
          <ul className="cart-list">
            {cart.map((item, index) => (
              <li key={index} className="cart-item">
                <span>{item.name}</span>
                <span className="price">₹{item.price}</span>
              </li>
            ))}
          </ul>

          <div className="cart-total">
            <strong>Total:</strong> ₹{total}
          </div>

          <button className="checkout-btn">Checkout</button>
        </>
      )}
    </div>
  );
}

export default Cart;
