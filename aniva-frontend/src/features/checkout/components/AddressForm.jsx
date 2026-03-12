import { useState } from "react";

function AddressForm() {

  const [address, setAddress] = useState("");

  return (

    <div className="checkout-address">

      <h3>Shipping Address</h3>

      <textarea
        value={address}
        onChange={(e) => setAddress(e.target.value)}
        placeholder="Enter your delivery address"
      />

    </div>

  );
}

export default AddressForm;