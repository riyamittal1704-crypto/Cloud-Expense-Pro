import { useState } from "react";
import { createExpense } from "../services/api";

function CreateExpense({ onExpenseCreated, onCancel }) {

    const [amount, setAmount] = useState("");
    const [category, setCategory] = useState("");
    const [expenseDate, setExpenseDate] = useState("");
    const [description, setDescription] = useState("");

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        setError("");
        setSuccess("");
        setLoading(true);

        try {
            const expenseData = {
                amount: Number(amount),
                category: category,
                expenseDate: expenseDate,
                description: description
            };

            await createExpense(expenseData);

            setSuccess("Expense created successfully!");

            setAmount("");
            setCategory("");
            setExpenseDate("");
            setDescription("");

            if (onExpenseCreated) {
                onExpenseCreated();
            }

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="create-expense-page">

            <div className="create-expense-card">

                <h1>Create Expense</h1>

                <p className="form-subtitle">
                    Submit a new business expense for approval.
                </p>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="success-message">
                        {success}
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    <label>Amount</label>

                    <input
                        type="number"
                        step="0.01"
                        min="0.01"
                        value={amount}
                        onChange={(event) =>
                            setAmount(event.target.value)
                        }
                        placeholder="Enter amount"
                        required
                    />

                    <label>Category</label>

                    <select
                        value={category}
                        onChange={(event) =>
                            setCategory(event.target.value)
                        }
                        required
                    >
                        <option value="">
                            Select category
                        </option>

                        <option value="Travel">
                            Travel
                        </option>

                        <option value="Food">
                            Food
                        </option>

                        <option value="Accommodation">
                            Accommodation
                        </option>

                        <option value="Transportation">
                            Transportation
                        </option>

                        <option value="Office Supplies">
                            Office Supplies
                        </option>

                        <option value="Other">
                            Other
                        </option>
                    </select>

                    <label>Expense Date</label>

                    <input
                        type="date"
                        value={expenseDate}
                        onChange={(event) =>
                            setExpenseDate(event.target.value)
                        }
                        required
                    />

                    <label>Description</label>

                    <textarea
                        value={description}
                        onChange={(event) =>
                            setDescription(event.target.value)
                        }
                        placeholder="Describe the expense"
                        rows="5"
                        maxLength="500"
                        required
                    />

                    <div className="form-buttons">

                        <button
                            type="button"
                            className="cancel-button"
                            onClick={onCancel}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="submit-button"
                            disabled={loading}
                        >
                            {loading
                                ? "Submitting..."
                                : "Submit Expense"}
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}

export default CreateExpense;