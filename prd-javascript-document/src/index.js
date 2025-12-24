import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./styles.css";

// Future providers (Theme / Redux / Router) can easily plug in here
const root = ReactDOM.createRoot(document.getElementById("root"));

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
