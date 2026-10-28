import { useEffect, useState } from "react";
import { getMyExpenses } from "../services/api";
import CreateExpense from "./CreateExpense";
import ReceiptUpload from "../components/ReceiptUpload";

function EmployeeDashboard() {
    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showCreateExpense, setShowCreateExpense] = useState(false);

    const user = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        loadExpenses();
    }, []);

    const loadExpenses = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await getMyExpenses();
            setExpenses(data);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.reload();
    };

    if (showCreateExpense) {
    return (
        <CreateExpense
            onCancel={() => setShowCreateExpense(false)}
            onExpenseCreated={() => {
                setShowCreateExpense(false);
                loadExpenses();
            }}
        />
    );
}

    return (
        <div className="dashboard-page">

            <header className="dashboard-header">
                <div>
                    <h1>CloudExpense Pro</h1>
                    <p>Employee Dashboard</p>
                </div>

                <div className="user-section">
                    <span>
                        Welcome, {user?.name}
                    </span>

                    <button onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </header>

            <main className="dashboard-content">

                <div className="dashboard-title">
                    <div>
                        <h2>My Expenses</h2>
                        <p>
                            View and track your submitted expenses.
                        </p>
                    </div>

                    <button
                        className="create-expense-button"
                        onClick={() => setShowCreateExpense(true)}
                    >
                        + Create Expense
                    </button>
                </div>

                {loading && (
                    <p className="loading-message">
                        Loading expenses...
                    </p>
                )}

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {!loading && !error && expenses.length === 0 && (
                    <div className="empty-state">
                        <h3>No expenses found</h3>
                        <p>
                            You haven't submitted any expenses yet.
                        </p>
                    </div>
                )}

                {!loading && !error && expenses.length > 0 && (
                    <div className="expense-table-container">

                        <table className="expense-table">

                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Amount</th>
                                    <th>Category</th>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Receipt</th>
                                </tr>
                            </thead>

                            <tbody>
                                {expenses.map((expense) => (
                                    <tr key={expense.id}>

                                        <td>
                                            #{expense.id}
                                        </td>

                                        <td>
                                            ₹{expense.amount}
                                        </td>

                                        <td>
                                            {expense.category}
                                        </td>

                                        <td>
                                            {expense.expenseDate}
                                        </td>

                                        <td>
                                            {expense.description}
                                        </td>

                                        <td>
                                            <span
                                                className={`status status-${expense.status.toLowerCase()}`}
                                            >
                                                {expense.status}
                                            </span>
                                        </td>

                                        <td>
                                            <ReceiptUpload
                                                expense={expense}
                                                onReceiptUploaded={(updatedExpense) => {
                                                    setExpenses((currentExpenses) =>
                                                        currentExpenses.map((item) =>
                                                            item.id === updatedExpense.id
                                                                ? updatedExpense
                                                                : item
                                                        )
                                                    );
                                                }}
                                            />
                                        </td>

                                    </tr>
                                ))}
                            </tbody>

                        </table>

                    </div>
                )}

            </main>

        </div>
    );
}

export default EmployeeDashboard;