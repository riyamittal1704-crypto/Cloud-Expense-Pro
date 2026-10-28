import { useState } from "react";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import EmployeeDashboard from "./pages/EmployeeDashboard";
import AdminDashboard from "./pages/AdminDashboard";

function App() {

    const [isLoggedIn, setIsLoggedIn] = useState(
        !!localStorage.getItem("token")
    );

    const [showSignup, setShowSignup] = useState(false);

    const user = JSON.parse(
        localStorage.getItem("user") || "null"
    );

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    if (!isLoggedIn) {

        if (showSignup) {
            return (
                <Signup
                    onBackToLogin={() => setShowSignup(false)}
                />
            );
        }

        return (
            <Login
                onLogin={handleLogin}
                onSignup={() => setShowSignup(true)}
            />
        );
    }

    if (user?.role === "ADMIN") {
        return <AdminDashboard />;
    }

    return <EmployeeDashboard />;
}

export default App;