import "../styles.css";

const products = [
  { id: 1, name: "Laptop", price: 55000 },
  { id: 2, name: "Headphones", price: 2500 },
  { id: 3, name: "Smartphone", price: 30000 },
];

function ProductList({ addToCart }) {
  return (
    <div className="product-card">
      <h2 className="product-title">🛍 Products</h2>

      {products.map((product) => (
        <div key={product.id} className="product-item">
          <div>
            <h4>{product.name}</h4>
            <p className="price">₹{product.price}</p>
          </div>

          <button
            className="add-btn"
            onClick={() => addToCart(product)}
          >
            ➕ Add
          </button>
        </div>
      ))}
    </div>
  );
}

export default ProductList;
