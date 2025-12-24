import React, { useState } from "react";
import ProductList from "./components/ProductList";
import Cart from "./components/Cart";
import "./styles.css";

function App() {
  const [cart, setCart] = useState([]);
  const [filterText, setFilterText] = useState("");
  const [showCart, setShowCart] = useState(true);
  const [category, setCategory] = useState("all");

  const addToCart = (product) => {
    setCart((prev) => [...prev, product]);
  };

  const clearCart = () => {
    setCart([]);
  };

  return (
    <div className="app">

      {/* Header */}
      <header className="header">
        <h1 className="brand-title">
          🛒 <span>Online Store</span>
        </h1>

        {/* CART TOGGLE */}
        <button
          className="toggle-cart-btn"
          onClick={() => setShowCart(!showCart)}
        >
          {showCart ? "Hide Cart" : "Show Cart"}
        </button>

        {/* CART COUNT */}
        <div className="cart-status">
          <strong>{cart.length}</strong> items
        </div>
      </header>

      {/* SEARCH & FILTER SECTION */}
      <section className="top-controls">

        {/* Search Box */}
        <input
          type="text"
          placeholder="Search products..."
          className="search-box"
          value={filterText}
          onChange={(e) => setFilterText(e.target.value)}
        />

        {/* Category Dropdown */}
        <select
          className="select-box"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="all">All Products</option>
          <option value="clothing">Clothing</option>
          <option value="electronics">Electronics</option>
          <option value="books">Books</option>
        </select>
      </section>

      {/* MAIN CONTENT */}
      <main className="content">

        {/* Product Section */}
        <ProductList
          addToCart={addToCart}
          filterText={filterText}
          category={category}
        />

        {/* Cart Section */}
        {showCart && (
          <Cart cart={cart} clearCart={clearCart} />
        )}
      </main>

      {/* FOOTER */}
      <footer className="footer">
        <p>Made with ❤️ using React — Happy Shopping!</p>
      </footer>

    </div>
  );
}

export default App;
