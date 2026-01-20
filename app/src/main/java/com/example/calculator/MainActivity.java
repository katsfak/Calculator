package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.*;

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
    private String fullExpression = "";   // Full expression for precedence evaluation

    // Formatter for displaying numbers with proper decimal places
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##########");

    // History Manangment
    private ArrayList<String> calculationHistory;  // Store all calculations
    private static final int MAX_HISTORY = 50;     // Limit history to 50 items

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
        calculationHistory = new ArrayList<>();
        
        // Load history from storage
        loadHistoryFromStorage();

        // Set up all button click listeners
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();
        setupScientificButtons();
        setupCopyPasteGestures();

    }

    /**
     * Called when activity is paused - saves history to storage
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveHistoryToStorage();
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
        // Add this for a History button if you have one
        findViewById(R.id.btnHistory).setOnClickListener(v -> showHistory());
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
     * Behavior: - Build expression with proper precedence handling
     * - Display the expression so far
     */
    private void setOperator(String op) {
        if (!currentNumber.isEmpty() && !lastInputWasOperator) {
            // Add current number to expression
            fullExpression += currentNumber + " " + op + " ";
            tvSecondary.setText(fullExpression);
            currentNumber = "";
            isNewOperation = true;
            lastInputWasOperator = true;
        } else if (!operator.isEmpty() && lastInputWasOperator) {
            // Allow changing operator if user presses different operator button
            fullExpression = fullExpression.substring(0, fullExpression.lastIndexOf(op)) + op + " ";
            tvSecondary.setText(fullExpression);
        }
        operator = op;
    }

    /**
     * Calculates and displays the result of the current operation
     * Uses proper operator precedence (PEMDAS/BODMAS)
     *
     * Supports: - Addition, subtraction, multiplication, division -
     * Power/exponentiation - Division by zero error handling - Proper precedence
     */
    private void calculateResult() {
        // Ensure we have an expression to evaluate
        if (currentNumber.isEmpty() && operator.isEmpty()) {
            return;
        }

        try {
            // Complete the expression with the current number
            String completeExpression = fullExpression + currentNumber;
            
            // Evaluate with proper precedence
            double result = evaluateExpression(completeExpression);
            
            // Format and display the result
            String resultStr = formatNumber(result);
            tvSecondary.setText(completeExpression);
            updateDisplay(resultStr);
            
            // Add to history
            String historyEntry = completeExpression + " = " + resultStr;
            addToHistory(historyEntry);

            // Reset for next calculation
            currentNumber = resultStr;
            fullExpression = "";
            operator = "";
            isNewOperation = true;
            lastInputWasOperator = false;
        } catch (Exception e) {
            updateDisplay("Error");
            fullExpression = "";
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
     * Handles parenthesis input with full evaluation support
     *
     * Behavior: - Adds opening parenthesis when count is 0 or after an operator
     * - Adds closing parenthesis to close open groups
     * - Tracks count of unmatched opening parentheses
     * - Parentheses are part of the expression and evaluated properly
     */
    private void handleParenthesis() {
        if (openParenthesisCount == 0 || lastInputWasOperator) {
            // Add opening parenthesis
            if (!currentNumber.isEmpty()) {
                fullExpression += currentNumber + " ( ";
            } else {
                fullExpression += "( ";
            }
            currentNumber = "";
            openParenthesisCount++;
            lastInputWasOperator = false;
        } else if (openParenthesisCount > 0) {
            // Add closing parenthesis
            if (!currentNumber.isEmpty()) {
                fullExpression += currentNumber + " ) ";
            } else {
                fullExpression += " ) ";
            }
            currentNumber = "";
            openParenthesisCount--;
            lastInputWasOperator = false;
        }
        
        // Display the expression with parentheses
        String displayText = fullExpression + currentNumber;
        tvSecondary.setText(displayText);
        updateDisplay(displayText);
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
        fullExpression = "";
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

    /**
     * Sets up long-press gestures on display for copy and paste functionality
     */
    private void setupCopyPasteGestures() {
        // Long-press on display to copy/paste using context menu
        tvDisplay.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Display Actions")
                    .setItems(new CharSequence[]{"Copy", "Paste"}, (dialog, which) -> {
                if (which == 0) {
                    copyToClipboard();
                } else if (which == 1) {
                    pasteFromClipboard();
                }
            })
                    .show();
            return true;
        });
    }

    /**
     * Copies the current display value to clipboard
     */
    private void copyToClipboard() {
        String textToCopy = tvDisplay.getText().toString();
        if (!textToCopy.isEmpty() && !textToCopy.equals("0") && !textToCopy.equals("Error")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Calculator Result", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied: " + textToCopy, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Pastes value from clipboard to the display
     */
    private void pasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            String pastedText = item.getText().toString().trim();

            // Validate that pasted text is a valid number
            try {
                Double.parseDouble(pastedText);
                // If it's a valid number, set it to display
                currentNumber = pastedText;
                isNewOperation = true;
                updateDisplay(currentNumber);
                Toast.makeText(this, "Pasted: " + pastedText, Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number in clipboard", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Records a calculation to history
     *
     * @param calculation The calculation string (e.g., "12 + 5 = 17")
     */
    private void addToHistory(String calculation) {
        if (calculationHistory.size() >= MAX_HISTORY) {
            calculationHistory.remove(0); // Remove oldest entry to maintain size
        }
        calculationHistory.add(calculation);
    }

    /**
     * Shows calculation history in a dialog
     */
    private void showHistory() {
        if (calculationHistory.isEmpty()) {
            Toast.makeText(this, "No history yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert ArrayList to array for dialog
        String[] historyArray = calculationHistory.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation History")
                .setItems(historyArray, (dialog, which) -> {
                    // Click item to paste result back to display
                    String selectedEntry = historyArray[which];
                    // Extract the result (after "=")
                    String result = selectedEntry.substring(selectedEntry.lastIndexOf("=") + 1).trim();
                    currentNumber = result;
                    isNewOperation = true;
                    updateDisplay(currentNumber);
                    Toast.makeText(MainActivity.this, "Loaded: " + result, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Clear History", (dialog, which) -> {
                    calculationHistory.clear();
                    saveHistoryToStorage();
                    Toast.makeText(MainActivity.this, "History cleared", Toast.LENGTH_SHORT).show();
                })
                .setPositiveButton("Close", null)
                .show();
    }

    /**
     * Saves history to SharedPreferences storage
     */
    private void saveHistoryToStorage() {
        SharedPreferences prefs = getSharedPreferences("calculator_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("history_count", calculationHistory.size());

        for (int i = 0; i < calculationHistory.size(); i++) {
            editor.putString("history_" + i, calculationHistory.get(i));
        }
        editor.apply();
    }

    /**
     * Loads history from SharedPreferences storage
     */
    private void loadHistoryFromStorage() {
        SharedPreferences prefs = getSharedPreferences("calculator_prefs", MODE_PRIVATE);
        int count = prefs.getInt("history_count", 0);

        for (int i = 0; i < count; i++) {
            String entry = prefs.getString("history_" + i, "");
            if (!entry.isEmpty()) {
                calculationHistory.add(entry);
            }
        }
    }

    // ===== Expression Evaluation with Proper Operator Precedence =====

    /**
     * Evaluates a complete expression with proper operator precedence
     * PEMDAS/BODMAS: Parentheses, Exponents, Multiplication/Division, Addition/Subtraction
     * 
     * @param expression The mathematical expression to evaluate
     * @return The result of the evaluation
     */
    private double evaluateExpression(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", ""); // Remove spaces
        expression = expression.replace("×", "*").replace("÷", "/"); // Normalize operators
        ExpressionEvaluator evaluator = new ExpressionEvaluator(expression);
        return evaluator.evaluate();
    }

    /**
     * Expression parser and evaluator using recursive descent parsing
     * Implements proper operator precedence
     */
    private class ExpressionEvaluator {
        private String expression;
        private int position = 0;

        ExpressionEvaluator(String expr) {
            this.expression = expr;
        }

        /**
         * Main evaluation method - starts with lowest precedence (addition/subtraction)
         */
        double evaluate() throws Exception {
            return parseAdditionSubtraction();
        }

        /**
         * Handles addition and subtraction (lowest precedence)
         */
        private double parseAdditionSubtraction() throws Exception {
            double result = parseMultiplicationDivision();

            while (position < expression.length() && (peek() == '+' || peek() == '-')) {
                char operator = expression.charAt(position++);
                double right = parseMultiplicationDivision();

                if (operator == '+') {
                    result += right;
                } else {
                    result -= right;
                }
            }
            return result;
        }

        /**
         * Handles multiplication and division (medium precedence)
         */
        private double parseMultiplicationDivision() throws Exception {
            double result = parseExponentiation();

            while (position < expression.length() && (peek() == '*' || peek() == '/')) {
                char operator = expression.charAt(position++);
                double right = parseExponentiation();

                if (operator == '*') {
                    result *= right;
                } else {
                    if (right == 0) {
                        throw new Exception("Division by zero");
                    }
                    result /= right;
                }
            }
            return result;
        }

        /**
         * Handles exponentiation (higher precedence, right-associative)
         */
        private double parseExponentiation() throws Exception {
            double result = parseUnary();

            if (position < expression.length() && peek() == '^') {
                position++; // consume '^'
                double right = parseExponentiation(); // Right-associative
                result = Math.pow(result, right);
            }
            return result;
        }

        /**
         * Handles unary operations (negative numbers)
         */
        private double parseUnary() throws Exception {
            if (position < expression.length() && peek() == '-') {
                position++;
                return -parseUnary();
            }
            if (position < expression.length() && peek() == '+') {
                position++;
                return parseUnary();
            }
            return parsePrimary();
        }

        /**
         * Handles parentheses and numbers (highest precedence)
         */
        private double parsePrimary() throws Exception {
            if (position < expression.length() && peek() == '(') {
                position++; // consume '('
                double result = parseAdditionSubtraction();
                if (position >= expression.length() || peek() != ')') {
                    throw new Exception("Mismatched parentheses");
                }
                position++; // consume ')'
                return result;
            }
            return parseNumber();
        }

        /**
         * Parses a number (integer or decimal)
         */
        private double parseNumber() throws Exception {
            StringBuilder number = new StringBuilder();

            while (position < expression.length() && 
                   (Character.isDigit(peek()) || peek() == '.')) {
                number.append(expression.charAt(position++));
            }

            if (number.length() == 0) {
                throw new Exception("Invalid expression");
            }

            return Double.parseDouble(number.toString());
        }

        /**
         * Peeks at the current character without consuming it
         */
        private char peek() {
            if (position >= expression.length()) {
                return '\0';
            }
            return expression.charAt(position);
        }
    }

}
