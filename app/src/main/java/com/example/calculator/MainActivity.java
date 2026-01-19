package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

/**
 * MainActivity - Samsung-style Calculator Application
 *
 * This calculator supports: - Basic arithmetic operations (addition,
 * subtraction, multiplication, division) - Scientific functions (trigonometric,
 * logarithmic, square root, factorial, power) - Percentage calculations -
 * Mathematical constants (π, e) - Automatic light/dark mode detection -
 * Toggleable scientific mode
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView tvDisplay;           // Main display showing current number/result
    private TextView tvSecondary;         // Secondary display showing previous operation
    private HorizontalScrollView scientificPanel; // Panel containing scientific function buttons

    // Calculator state variables
    private String currentNumber = "";    // Current number being entered by user
    private String operator = "";         // Current operator (+, -, ×, ÷, ^)
    private double firstOperand = 0;      // First operand for binary operations
    private boolean isNewOperation = true; // Flag to clear display on next number input
    private boolean isScientificMode = false; // Flag indicating if scientific mode is active
    private boolean lastInputWasOperator = false; // Tracks if last button pressed was an operator
    private int openParenthesisCount = 0; // Counter for unmatched parentheses

    // Formatter for displaying numbers with proper decimal places
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##########");

    /**
     * Called when the activity is first created Initializes UI components and
     * sets up all button listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        tvDisplay = findViewById(R.id.tvDisplay);
        tvSecondary = findViewById(R.id.tvSecondary);
        scientificPanel = findViewById(R.id.scientificPanel);

        // Set up all button click listeners
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();
        setupScientificButtons();
    }

    /**
     * Sets up click listeners for number buttons (0-9) and decimal point Uses a
     * loop to avoid repetitive code for each number button
     */
    private void setupNumberButtons() {
        // Array of all number button IDs
        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        // Attach click listener to each number button
        for (int id : numberIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                String number = ((Button) v).getText().toString();
                appendNumber(number);
            });
        }

        // Decimal point button
        findViewById(R.id.btnDot).setOnClickListener(v -> appendNumber("."));
    }

    /**
     * Sets up click listeners for basic operator buttons Includes: +, -, ×, ÷,
     * and equals
     */
    private void setupOperatorButtons() {
        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("÷"));
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
    }

    /**
     * Sets up click listeners for function buttons Includes: AC (clear all),
     * percentage, parentheses, and scientific mode toggle
     */
    private void setupFunctionButtons() {
        findViewById(R.id.btnAC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnPercent).setOnClickListener(v -> calculatePercent());
        findViewById(R.id.btnParenthesis).setOnClickListener(v -> handleParenthesis());
        findViewById(R.id.btnToggleScientific).setOnClickListener(v -> toggleScientificMode());
    }

    /**
     * Sets up click listeners for scientific function buttons Includes:
     * trigonometric functions, logarithms, square root, constants, factorial,
     * and power
     */
    private void setupScientificButtons() {
        findViewById(R.id.btnSin).setOnClickListener(v -> calculateTrigonometric("sin"));
        findViewById(R.id.btnCos).setOnClickListener(v -> calculateTrigonometric("cos"));
        findViewById(R.id.btnTan).setOnClickListener(v -> calculateTrigonometric("tan"));
        findViewById(R.id.btnLog).setOnClickListener(v -> calculateLogarithm("log"));
        findViewById(R.id.btnLn).setOnClickListener(v -> calculateLogarithm("ln"));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> calculateSquareRoot());
        findViewById(R.id.btnPi).setOnClickListener(v -> insertConstant(Math.PI));
        findViewById(R.id.btnE).setOnClickListener(v -> insertConstant(Math.E));
        findViewById(R.id.btnFactorial).setOnClickListener(v -> calculateFactorial());
        findViewById(R.id.btnPower).setOnClickListener(v -> setOperator("^"));
    }

    /**
     * Appends a digit or decimal point to the current number
     *
     * @param number The digit or decimal point to append
     *
     * Handles: - Starting a new number after an operation - Preventing multiple
     * decimal points - Replacing leading zero with the new digit
     */
    private void appendNumber(String number) {
        // If starting a new operation, clear the current number
        if (isNewOperation) {
            currentNumber = "";
            isNewOperation = false;
        }

        // Prevent multiple decimal points in the same number
        if (number.equals(".") && currentNumber.contains(".")) {
            return;
        }

        // Replace leading zero with new digit (except for decimals like 0.5)
        if (currentNumber.equals("0") && !number.equals(".")) {
            currentNumber = number;
        } else {
            currentNumber += number;
        }

        updateDisplay(currentNumber);
        lastInputWasOperator = false;
    }

    /**
     * Sets the operator for a binary operation
     *
     * @param op The operator symbol (+, -, ×, ÷, ^)
     *
     * Behavior: - If there's a pending operation, calculate it first (chain
     * operations) - Store the first operand and operator - Allow changing
     * operator if user presses different operator consecutively
     */
    private void setOperator(String op) {
        if (!currentNumber.isEmpty() && !lastInputWasOperator) {
            // If there's already an operator, calculate the pending operation first
            if (!operator.isEmpty()) {
                calculateResult();
            }
            // Store the first operand and set the new operator
            firstOperand = Double.parseDouble(currentNumber);
            operator = op;
            tvSecondary.setText(formatNumber(firstOperand) + " " + operator);
            isNewOperation = true;
            lastInputWasOperator = true;
        } else if (!operator.isEmpty() && lastInputWasOperator) {
            // Allow changing operator if user presses different operator button
            operator = op;
            tvSecondary.setText(formatNumber(firstOperand) + " " + operator);
        }
    }

    /**
     * Calculates and displays the result of the current operation
     *
     * Supports: - Addition, subtraction, multiplication, division -
     * Power/exponentiation - Division by zero error handling - Chaining
     * operations
     */
    private void calculateResult() {
        // Ensure we have both operands and an operator
        if (currentNumber.isEmpty() || operator.isEmpty()) {
            return;
        }

        try {
            double secondOperand = Double.parseDouble(currentNumber);
            double result = 0;

            // Perform the calculation based on the operator
            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                    // Check for division by zero
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        updateDisplay("Error");
                        return;
                    }
                    break;
                case "^":
                    // Power/exponentiation operation
                    result = Math.pow(firstOperand, secondOperand);
                    break;
            }

            // Format and display the result
            String resultStr = formatNumber(result);
            tvSecondary.setText(formatNumber(firstOperand) + " " + operator + " " + formatNumber(secondOperand));
            updateDisplay(resultStr);

            // Store result for potential chaining
            currentNumber = resultStr;
            operator = "";
            isNewOperation = true;
            lastInputWasOperator = false;
        } catch (Exception e) {
            updateDisplay("Error");
        }
    }

    /**
     * Calculates percentage of the current number Divides the current number by
     * 100
     */
    private void calculatePercent() {
        if (!currentNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(currentNumber);
                double result = value / 100;
                String resultStr = formatNumber(result);
                updateDisplay(resultStr);
                currentNumber = resultStr;
            } catch (Exception e) {
                updateDisplay("Error");
            }
        }
    }

    /**
     * Calculates trigonometric functions (sin, cos, tan)
     *
     * @param function The trigonometric function name ("sin", "cos", or "tan")
     *
     * Note: Input is in degrees, converted to radians for calculation
     */
    private void calculateTrigonometric(String function) {
        if (!currentNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(currentNumber);
                // Convert degrees to radians for trigonometric functions
                double radians = Math.toRadians(value);
                double result = 0;

                // Calculate based on the function type
                switch (function) {
                    case "sin":
                        result = Math.sin(radians);
                        break;
                    case "cos":
                        result = Math.cos(radians);
                        break;
                    case "tan":
                        result = Math.tan(radians);
                        break;
                }

                // Display the result and show the operation in secondary display
                String resultStr = formatNumber(result);
                tvSecondary.setText(function + "(" + formatNumber(value) + ")");
                updateDisplay(resultStr);
                currentNumber = resultStr;
                isNewOperation = true;
            } catch (Exception e) {
                updateDisplay("Error");
            }
        }
    }

    /**
     * Calculates logarithmic functions
     *
     * @param type The logarithm type: "log" (base 10) or "ln" (natural
     * logarithm)
     *
     * Error handling: Displays error for non-positive numbers (log domain
     * error)
     */
    private void calculateLogarithm(String type) {
        if (!currentNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(currentNumber);
                // Logarithm is only defined for positive numbers
                if (value <= 0) {
                    updateDisplay("Error");
                    return;
                }

                // Calculate log base 10 or natural logarithm
                double result = type.equals("log") ? Math.log10(value) : Math.log(value);
                String resultStr = formatNumber(result);
                tvSecondary.setText(type + "(" + formatNumber(value) + ")");
                updateDisplay(resultStr);
                currentNumber = resultStr;
                isNewOperation = true;
            } catch (Exception e) {
                updateDisplay("Error");
            }
        }
    }

    /**
     * Calculates square root of the current number
     *
     * Error handling: Displays error for negative numbers (imaginary result)
     */
    private void calculateSquareRoot() {
        if (!currentNumber.isEmpty()) {
            try {
                double value = Double.parseDouble(currentNumber);
                // Square root is not defined for negative numbers in real numbers
                if (value < 0) {
                    updateDisplay("Error");
                    return;
                }

                double result = Math.sqrt(value);
                String resultStr = formatNumber(result);
                tvSecondary.setText("√(" + formatNumber(value) + ")");
                updateDisplay(resultStr);
                currentNumber = resultStr;
                isNewOperation = true;
            } catch (Exception e) {
                updateDisplay("Error");
            }
        }
    }

    /**
     * Calculates factorial of the current number
     *
     * Formula: n! = n × (n-1) × (n-2) × ... × 2 × 1
     *
     * Restrictions: - Only works for non-negative integers - Limited to n ≤ 20
     * to prevent overflow
     */
    private void calculateFactorial() {
        if (!currentNumber.isEmpty()) {
            try {
                int value = (int) Double.parseDouble(currentNumber);
                // Factorial restrictions: must be non-negative and not too large
                if (value < 0 || value > 20) {
                    updateDisplay("Error");
                    return;
                }

                // Calculate factorial using iterative approach
                long result = 1;
                for (int i = 2; i <= value; i++) {
                    result *= i;
                }

                String resultStr = String.valueOf(result);
                tvSecondary.setText(value + "!");
                updateDisplay(resultStr);
                currentNumber = resultStr;
                isNewOperation = true;
            } catch (Exception e) {
                updateDisplay("Error");
            }
        }
    }

    /**
     * Inserts a mathematical constant (π or e) into the calculator
     *
     * @param constant The constant value (Math.PI or Math.E)
     */
    private void insertConstant(double constant) {
        String constantStr = formatNumber(constant);
        if (isNewOperation) {
            currentNumber = constantStr;
            isNewOperation = false;
        } else {
            currentNumber = constantStr;
        }
        updateDisplay(currentNumber);
    }

    /**
     * Handles parenthesis input
     *
     * Simple implementation: - Adds opening parenthesis when count is 0 or
     * after an operator - Adds closing parenthesis otherwise - Tracks count of
     * unmatched opening parentheses
     *
     * Note: This is a simplified version for display purposes only
     */
    private void handleParenthesis() {
        if (openParenthesisCount == 0 || lastInputWasOperator) {
            currentNumber += "(";
            openParenthesisCount++;
        } else {
            currentNumber += ")";
            openParenthesisCount--;
        }
        updateDisplay(currentNumber);
    }

    /**
     * Toggles between basic and scientific calculator modes
     *
     * Changes: - Shows/hides the scientific function panel - Updates the toggle
     * button text (SCI ↔ BSC)
     */
    private void toggleScientificMode() {
        isScientificMode = !isScientificMode;
        // Show or hide the scientific functions panel
        scientificPanel.setVisibility(isScientificMode ? View.VISIBLE : View.GONE);
        // Update button text to indicate current mode
        Button btn = findViewById(R.id.btnToggleScientific);
        btn.setText(isScientificMode ? "BSC" : "SCI");
    }

    /**
     * Clears all calculator state and resets to initial values
     *
     * Resets: - Current number and operator - First operand - All state flags -
     * Display (shows "0") - Secondary display (empty)
     */
    private void clearAll() {
        currentNumber = "";
        operator = "";
        firstOperand = 0;
        isNewOperation = true;
        lastInputWasOperator = false;
        openParenthesisCount = 0;
        updateDisplay("0");
        tvSecondary.setText("");
    }

    /**
     * Formats a number for display
     *
     * @param number The number to format
     * @return Formatted string representation
     *
     * Behavior: - If the number is a whole number, display without decimal
     * point - Otherwise, display with appropriate decimal places (up to 10
     * digits)
     */
    private String formatNumber(double number) {
        // Check if number is a whole number (integer)
        if (number == (long) number) {
            return String.format("%d", (long) number);
        } else {
            // Format with decimal places, removing trailing zeros
            String formatted = decimalFormat.format(number);
            return formatted;
        }
    }

    /**
     * Updates the main display with the given text
     *
     * @param text The text to display (number, result, or error message)
     */
    private void updateDisplay(String text) {
        tvDisplay.setText(text);
    }
}
