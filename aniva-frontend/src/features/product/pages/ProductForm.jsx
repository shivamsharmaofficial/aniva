import { useState } from "react";
import { useCategories } from "@/features/product/hooks/useCategories";

import {
  DndContext,
  closestCenter
} from "@dnd-kit/core";

import {
  SortableContext,
  useSortable,
  arrayMove,
  verticalListSortingStrategy
} from "@dnd-kit/sortable";

import { CSS } from "@dnd-kit/utilities";

import "@/features/product/styles/createProduct.css";

/* =====================================================
   INITIAL FORM STATE
===================================================== */

const buildInitialForm = (data) => ({
  name: data?.name || "",
  description: data?.description || "",
  brand: data?.brand || "",
  fragranceType: data?.fragranceType || "",
  burnTime: data?.burnTime || "",
  weightGrams: data?.weightGrams || "",
  price: data?.price || "",
  discountPrice: data?.discountPrice || "",
  categoryId: data?.categoryId || "",
  isActive: data?.isActive ?? true,

  attributes: data?.attributes || [],

  images:
    data?.images?.map((img, i) => ({
      ...img,
      id: img.id || `${Date.now()}-${i}`
    })) || [],

  variants:
    data?.variants?.length > 0
      ? data.variants
      : [
          {
            variantName: "",
            sku: "",
            variantPrice: "",
            stockQuantity: "",
            weightGrams: "",
            isActive: true
          }
        ]
});

/* =====================================================
   SORTABLE IMAGE
===================================================== */

function SortableImage({ img, index, form, setForm }) {
  const { attributes, listeners, setNodeRef, transform, transition } =
    useSortable({ id: img.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition
  };

  const setPrimary = () => {
    const updated = form.images.map((image, idx) => ({
      ...image,
      isPrimary: idx === index
    }));

    setForm((prev) => ({ ...prev, images: updated }));
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="image-wrapper"
      {...attributes}
      {...listeners}
    >
      <img src={img.imageUrl} alt="preview" className="image-preview" />

      <button type="button" onClick={setPrimary}>
        {img.isPrimary ? "Primary" : "Set Primary"}
      </button>
    </div>
  );
}

/* =====================================================
   MAIN COMPONENT
===================================================== */

function ProductForm({ initialData = null, onSubmit, loading }) {

  /* ================= CATEGORY FETCH ================= */

  const { data } = useCategories();

  // SAFE ARRAY EXTRACTION
  const categories =
    data?.data ||
    data?.content ||
    (Array.isArray(data) ? data : []) ||
    [];

  /* ================= STATE ================= */

  const [form, setForm] = useState(() => buildInitialForm(initialData));
  const [errors, setErrors] = useState({});

  /* ================= VALIDATION ================= */

  const validate = () => {
    const e = {};

    if (!form.name.trim()) e.name = "Name required";
    if (!form.description.trim()) e.description = "Description required";

    if (!form.price || Number(form.price) <= 0)
      e.price = "Valid price required";

    if (
      form.discountPrice &&
      Number(form.discountPrice) >= Number(form.price)
    )
      e.discountPrice = "Discount must be less than price";

    if (!form.categoryId) e.categoryId = "Select category";

    form.variants.forEach((v, i) => {
      if (!v.variantName) e[`variantName-${i}`] = "Required";
      if (!v.sku) e[`sku-${i}`] = "Required";
      if (!v.variantPrice) e[`variantPrice-${i}`] = "Required";
    });

    setErrors(e);
    return e;
  };

  const scrollToFirstError = (err) => {
    const key = Object.keys(err)[0];
    if (!key) return;

    const el = document.querySelector(`[name="${key}"]`);
    if (el) el.scrollIntoView({ behavior: "smooth" });
  };

  /* ================= SUBMIT ================= */

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validation = validate();

    if (Object.keys(validation).length > 0) {
      scrollToFirstError(validation);
      return;
    }

    await onSubmit(form);
  };

  /* ================= IMAGE UPLOAD ================= */

  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);

    const newImages = files.map((file, index) => ({
      id: `${Date.now()}-${index}`,
      imageUrl: URL.createObjectURL(file),
      isPrimary: form.images.length === 0 && index === 0,
      displayOrder: form.images.length + index
    }));

    setForm((prev) => ({
      ...prev,
      images: [...prev.images, ...newImages]
    }));
  };

  /* ================= IMAGE DRAG ================= */

  const handleDragEnd = (event) => {
    const { active, over } = event;

    if (!over || active.id === over.id) return;

    const oldIndex = form.images.findIndex((img) => img.id === active.id);
    const newIndex = form.images.findIndex((img) => img.id === over.id);

    const reordered = arrayMove(form.images, oldIndex, newIndex).map(
      (img, index) => ({
        ...img,
        displayOrder: index
      })
    );

    setForm((prev) => ({
      ...prev,
      images: reordered
    }));
  };

  /* ================= VARIANTS ================= */

  const updateVariant = (i, field, value) => {
    setForm((prev) => {
      const updated = [...prev.variants];
      updated[i][field] = value;
      return { ...prev, variants: updated };
    });
  };

  const addVariant = () => {
    setForm((prev) => ({
      ...prev,
      variants: [
        ...prev.variants,
        {
          variantName: "",
          sku: "",
          variantPrice: "",
          stockQuantity: "",
          weightGrams: "",
          isActive: true
        }
      ]
    }));
  };

  /* =====================================================
     UI
  ===================================================== */

  return (
    <div className="admin-container">
      <form onSubmit={handleSubmit} noValidate>

        {/* BASIC INFO */}

        <div className="card">
          <h2>Basic Information</h2>

          <input
            name="name"
            placeholder="Product Name"
            value={form.name}
            onChange={(e) =>
              setForm((p) => ({ ...p, name: e.target.value }))
            }
          />
          <span className="error-text">{errors.name}</span>

          <textarea
            name="description"
            placeholder="Description"
            value={form.description}
            onChange={(e) =>
              setForm((p) => ({ ...p, description: e.target.value }))
            }
          />
          <span className="error-text">{errors.description}</span>

          <input
            placeholder="Brand"
            value={form.brand}
            onChange={(e) =>
              setForm((p) => ({ ...p, brand: e.target.value }))
            }
          />

          <input
            type="number"
            name="price"
            placeholder="Price"
            value={form.price}
            onChange={(e) =>
              setForm((p) => ({ ...p, price: e.target.value }))
            }
          />
          <span className="error-text">{errors.price}</span>

          <input
            type="number"
            name="discountPrice"
            placeholder="Discount Price"
            value={form.discountPrice}
            onChange={(e) =>
              setForm((p) => ({ ...p, discountPrice: e.target.value }))
            }
          />
          <span className="error-text">{errors.discountPrice}</span>

          {/* CATEGORY */}

          <select
            value={form.categoryId}
            onChange={(e) =>
              setForm((p) => ({ ...p, categoryId: e.target.value }))
            }
          >
            <option value="">Select Category</option>

            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>

          <span className="error-text">{errors.categoryId}</span>
        </div>

        {/* IMAGES */}

        <div className="card">
          <h2>Images (Drag to Reorder)</h2>

          <input type="file" multiple onChange={handleImageUpload} />

          <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
            <SortableContext
              items={form.images.map((img) => img.id)}
              strategy={verticalListSortingStrategy}
            >
              <div className="image-preview-grid">
                {form.images.map((img, i) => (
                  <SortableImage
                    key={img.id}
                    img={img}
                    index={i}
                    form={form}
                    setForm={setForm}
                  />
                ))}
              </div>
            </SortableContext>
          </DndContext>
        </div>

        {/* VARIANTS */}

        <div className="card">
          <h2>Variants</h2>

          {form.variants.map((v, i) => (
            <div key={i} className="variant-block">

              <input
                placeholder="Variant Name"
                value={v.variantName}
                onChange={(e) =>
                  updateVariant(i, "variantName", e.target.value)
                }
              />
              <span className="error-text">{errors[`variantName-${i}`]}</span>

              <input
                placeholder="SKU"
                value={v.sku}
                onChange={(e) =>
                  updateVariant(i, "sku", e.target.value)
                }
              />
              <span className="error-text">{errors[`sku-${i}`]}</span>

              <input
                type="number"
                placeholder="Variant Price"
                value={v.variantPrice}
                onChange={(e) =>
                  updateVariant(i, "variantPrice", e.target.value)
                }
              />

            </div>
          ))}

          <button type="button" onClick={addVariant}>
            + Add Variant
          </button>
        </div>

        <button type="submit" disabled={loading}>
          {loading ? "Saving..." : "Save Product"}
        </button>

      </form>
    </div>
  );
}

export default ProductForm;