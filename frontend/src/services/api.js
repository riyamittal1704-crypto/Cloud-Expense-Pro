const API_BASE_URL = "/api";


// =========================
// AUTHENTICATION
// =========================

export async function loginUser(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email,
            password
        })
    });

    const text = await response.text();

    let data;

    try {
        data = text ? JSON.parse(text) : null;
    } catch {
        data = text;
    }

    if (!response.ok) {
        throw new Error(
            typeof data === "string" && data
                ? data
                : `Login failed (${response.status})`
        );
    }

    if (!data) {
        throw new Error("Login failed: server returned an empty response.");
    }

    return data;
}

export async function registerUser(name, email, password) {
    const response = await fetch(
        `${API_BASE_URL}/users/register`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name,
                email,
                password
            })
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Registration failed"
        );
    }

    return data;
}

// =========================
// EMPLOYEE EXPENSES
// =========================

export async function getMyExpenses() {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/expenses`,
        {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to fetch expenses"
        );
    }

    return data;
}


export async function createExpense(expenseData) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/expenses`,
        {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(expenseData)
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to create expense"
        );
    }

    return data;
}


// =========================
// EMPLOYEE RECEIPTS
// =========================

export async function uploadReceipt(expenseId, file) {
    const token = localStorage.getItem("token");

    const formData = new FormData();

    formData.append("file", file);

    const response = await fetch(
        `${API_BASE_URL}/expenses/${expenseId}/receipt`,
        {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            },
            body: formData
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to upload receipt"
        );
    }

    return data;
}


export async function downloadReceipt(expenseId) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/expenses/${expenseId}/receipt`,
        {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        let message = "Failed to download receipt";

        try {
            const data = await response.json();

            message =
                typeof data === "string"
                    ? data
                    : message;

        } catch {
            // Response was not JSON
        }

        throw new Error(message);
    }

    return await response.blob();
}


// =========================
// ADMIN EXPENSES
// =========================

export async function getAllExpenses() {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/admin/expenses`,
        {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to fetch admin expenses"
        );
    }

    return data;
}


export async function approveExpense(expenseId) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/admin/expenses/${expenseId}/approve`,
        {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to approve expense"
        );
    }

    return data;
}


export async function rejectExpense(expenseId, reason) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/admin/expenses/${expenseId}/reject`,
        {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                reason: reason
            })
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : "Failed to reject expense"
        );
    }

    return data;
}


// =========================
// ADMIN RECEIPTS
// =========================

export async function downloadAdminReceipt(expenseId) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}/admin/expenses/${expenseId}/receipt`,
        {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        throw new Error(
            "Failed to download receipt"
        );
    }

    return await response.blob();
}