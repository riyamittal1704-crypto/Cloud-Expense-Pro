import { useState } from "react";
import {
    uploadReceipt,
    downloadReceipt
} from "../services/api";

function ReceiptUpload({ expense, onReceiptUploaded }) {

    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [downloading, setDownloading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleFileChange = (event) => {
        setError("");
        setSuccess("");

        const selectedFile = event.target.files[0];

        if (!selectedFile) {
            setFile(null);
            return;
        }

        setFile(selectedFile);
    };

    const handleUpload = async () => {

        if (!file) {
            setError("Please select a receipt file.");
            return;
        }

        setError("");
        setSuccess("");
        setUploading(true);

        try {

            const updatedExpense = await uploadReceipt(
                expense.id,
                file
            );

            setSuccess("Receipt uploaded successfully.");
            setFile(null);

            if (onReceiptUploaded) {
                onReceiptUploaded(updatedExpense);
            }

        } catch (error) {
            setError(error.message);
        } finally {
            setUploading(false);
        }
    };

    const handleDownload = async () => {

        setError("");
        setDownloading(true);

        try {

            const blob = await downloadReceipt(expense.id);

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
        } finally {
            setDownloading(false);
        }
    };

    return (
        <div className="receipt-section">

            {expense.receiptFileName ? (

                <div>

                    <span className="receipt-name">
                        {expense.receiptFileName}
                    </span>

                    <button
                        className="receipt-button"
                        onClick={handleDownload}
                        disabled={downloading}
                    >
                        {downloading
                            ? "Downloading..."
                            : "View / Download"}
                    </button>

                </div>

            ) : (

                <div>

                    <input
                        type="file"
                        accept=".pdf,.jpg,.jpeg,.png"
                        onChange={handleFileChange}
                    />

                    <button
                        className="receipt-button"
                        onClick={handleUpload}
                        disabled={uploading || !file}
                    >
                        {uploading
                            ? "Uploading..."
                            : "Upload Receipt"}
                    </button>

                </div>

            )}

            {success && (
                <div className="success-message">
                    {success}
                </div>
            )}

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

        </div>
    );
}

export default ReceiptUpload;