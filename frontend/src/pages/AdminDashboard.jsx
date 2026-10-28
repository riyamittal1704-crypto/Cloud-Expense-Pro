import { useEffect, useState } from "react";
import {
    getAllExpenses,
    approveExpense,
    rejectExpense,
    downloadAdminReceipt
} from "../services/api";

function AdminDashboard() {

    const [expenses, setExpenses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [rejectingExpenseId, setRejectingExpenseId] = useState(null);
    const [rejectionReason, setRejectionReason] = useState("");

    const user = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        loadExpenses();
    }, []);

    const loadExpenses = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await getAllExpenses();

            setExpenses(data);

        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const handleApprove = async (expenseId) => {

        try {
            setError("");

            await approveExpense(expenseId);

            await loadExpenses();

        } catch (error) {
            setError(error.message);
        }
    };

    const handleReject = async (expenseId) => {

        if (!rejectionReason.trim()) {
            setError("Please enter a rejection reason.");
            return;
        }

        try {
            setError("");

            await rejectExpense(
                expenseId,
                rejectionReason
            );

            setRejectingExpenseId(null);
            setRejectionReason("");

            await loadExpenses();

        } catch (error) {
            setError(error.message);
        }
    };

    const handleDownloadReceipt = async (expense) => {

        try {

            setError("");

            const blob = await downloadAdminReceipt(expense.id);

            const url = window.URL.createObjectURL(blob);

            const link = document.createElement("a");

            link.href = url;

            link.download =
                expense.receiptFileName || "receipt";

            document.body.appendChild(link);

            link.click();

            link.remove();

            window.URL.revokeObjectURL(url);

        } catch (error) {
            setError(error.message);
        }
    };

    const handleLogout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("user");

        window.location.reload();
    };

    return (
        <div className="dashboard-page">

            <header className="dashboard-header">

                <div>
                    <h1>CloudExpense Pro</h1>
                    <p>Administrator Dashboard</p>
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
                        <h2>All Expenses</h2>

                        <p>
                            Review and manage employee expenses.
                        </p>
                    </div>

                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {loading && (
                    <p className="loading-message">
                        Loading expenses...
                    </p>
                )}

                {!loading && !error && expenses.length === 0 && (
                    <div className="empty-state">

                        <h3>No expenses found</h3>

                        <p>
                            There are currently no submitted expenses.
                        </p>

                    </div>
                )}

                {!loading && expenses.length > 0 && (

                    <div className="expense-table-container">

                        <table className="expense-table">

                            <thead>

                                <tr>
                                    <th>ID</th>
                                    <th>Employee</th>
                                    <th>Amount</th>
                                    <th>Category</th>
                                    <th>Date</th>
                                    <th>Description</th>
                                    <th>Status</th>
                                    <th>Receipt</th>
                                    <th>Action</th>
                                </tr>

                            </thead>

                            <tbody>

                                {expenses.map((expense) => (

                                    <tr key={expense.id}>

                                        <td>
                                            #{expense.id}
                                        </td>

                                        <td>
                                            <strong>
                                                {expense.employeeName}
                                            </strong>
                                            <br />
                                            <small>
                                                Employee ID: {expense.employeeId}
                                            </small>
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

                                            {expense.status === "REJECTED" &&
                                                expense.rejectionReason && (
                                                    <div className="rejection-reason">
                                                        Reason:{" "}
                                                        {expense.rejectionReason}
                                                    </div>
                                                )}

                                        </td>

                                        <td>

                                            {expense.receiptFileName ? (

                                                <button
                                                    className="receipt-button"
                                                    onClick={() =>
                                                        handleDownloadReceipt(
                                                            expense
                                                        )
                                                    }
                                                >
                                                    View Receipt
                                                </button>

                                            ) : (

                                                <span>
                                                    No receipt
                                                </span>

                                            )}

                                        </td>

                                        <td>

                                            {expense.status === "PENDING" && (

                                                <div className="admin-actions">

                                                    <button
                                                        className="approve-button"
                                                        onClick={() =>
                                                            handleApprove(
                                                                expense.id
                                                            )
                                                        }
                                                    >
                                                        Approve
                                                    </button>

                                                    <button
                                                        className="reject-button"
                                                        onClick={() => {
                                                            setRejectingExpenseId(
                                                                expense.id
                                                            );
                                                            setRejectionReason(
                                                                ""
                                                            );
                                                        }}
                                                    >
                                                        Reject
                                                    </button>

                                                </div>

                                            )}

                                            {expense.status !== "PENDING" && (
                                                <span>
                                                    No action
                                                </span>
                                            )}

                                        </td>

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}

            </main>

            {rejectingExpenseId !== null && (

                <div className="modal-overlay">

                    <div className="reject-modal">

                        <h2>Reject Expense</h2>

                        <p>
                            Please provide a reason for rejecting
                            this expense.
                        </p>

                        <textarea
                            value={rejectionReason}
                            onChange={(event) =>
                                setRejectionReason(
                                    event.target.value
                                )
                            }
                            placeholder="Enter rejection reason"
                            rows="5"
                            maxLength="500"
                        />

                        <div className="form-buttons">

                            <button
                                className="cancel-button"
                                onClick={() => {
                                    setRejectingExpenseId(null);
                                    setRejectionReason("");
                                }}
                            >
                                Cancel
                            </button>

                            <button
                                className="reject-button"
                                onClick={() =>
                                    handleReject(
                                        rejectingExpenseId
                                    )
                                }
                            >
                                Confirm Rejection
                            </button>

                        </div>

                    </div>

                </div>

            )}

        </div>
    );
}

export default AdminDashboard;