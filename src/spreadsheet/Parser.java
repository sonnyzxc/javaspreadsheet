package spreadsheet;

import static spreadsheet.Associativity.LEFT;
import static spreadsheet.BinaryOperatorApplication.Operator.ADD;
import static spreadsheet.BinaryOperatorApplication.Operator.DIV;
import static spreadsheet.BinaryOperatorApplication.Operator.LPAREN;
import static spreadsheet.BinaryOperatorApplication.Operator.MUL;
import static spreadsheet.BinaryOperatorApplication.Operator.POW;
import static spreadsheet.BinaryOperatorApplication.Operator.RPAREN;
import static spreadsheet.BinaryOperatorApplication.Operator.SUB;

import common.api.Expression;
import common.lexer.InvalidTokenException;
import common.lexer.Lexer;
import common.lexer.Token;
import common.lexer.Token.Kind;
import java.util.EmptyStackException;
import java.util.Stack;
import spreadsheet.BinaryOperatorApplication.Operator;

public class Parser {

  private static final Stack<Expression> operandStack = new Stack<>();
  private static final Stack<Operator> operatorStack = new Stack<>();

  /**
   * Parse a string into an Expression.
   *
   * <p>DO NOT CHANGE THE SIGNATURE. The test suite depends on this.
   */
  static Expression parse(String input) throws InvalidSyntaxException {
    Lexer lexer = new Lexer(input);

    try {
      for (Token t = lexer.nextToken(); t != null; t = lexer.nextToken()) {
        switch (t.kind) {
          case NUMBER -> operandStack.push(new Number(t.numberValue));
          case CELL_LOCATION -> operandStack.push(new CellReference(t.cellLocationValue));
          case LPARENTHESIS -> operatorStack.push(tokenToOperator(t));
          case PLUS, MINUS, STAR, SLASH, CARET, RPARENTHESIS -> {
            while (!operatorStack.isEmpty() && precedes(tokenToOperator(t).getPrecedence())) {
              makeBinOpApp();
            }
            checkRightParenthesis(t);
          }
          default -> throw new InvalidSyntaxException(
              "Invalid token detected. Your token must be a number, cell location or one of "
                  + "these operators: + - * / ^ ( ) \nBut you inputted: " + t);
        }
      }
      while (!operatorStack.empty()) {
        makeBinOpApp();
      }
      return operandStack.peek();
    } catch (InvalidTokenException e) {
      throw new InvalidSyntaxException("Invalid Token: " + e);
    }
  }

  private static Operator tokenToOperator(Token t) throws InvalidSyntaxException {
    return switch (t.kind) {
      case PLUS -> ADD;
      case MINUS -> SUB;
      case STAR -> MUL;
      case SLASH -> DIV;
      case CARET -> POW;
      case LPARENTHESIS -> LPAREN;
      case RPARENTHESIS -> RPAREN;
      default -> throw new InvalidSyntaxException("Error Token: " + t);
    };
  }

  private static boolean precedes(int currentPrecedence) {
    return operatorStack.peek().getPrecedence() > currentPrecedence
        || (operatorStack.peek().getPrecedence() == currentPrecedence
        && operatorStack.peek().getAssociativity().equals(LEFT));
  }

  private static void checkRightParenthesis(Token t)
      throws InvalidSyntaxException {
    if (t.kind != Kind.RPARENTHESIS) {
      operatorStack.push(tokenToOperator(t));
    } else {
      try {
        operatorStack.pop();
      } catch (EmptyStackException e) {
        throw new InvalidSyntaxException("Matching left parenthesis not found.");
      }
    }
  }

  private static void makeBinOpApp()
      throws InvalidSyntaxException {
    try {
      Expression exp2 = operandStack.pop();
      Expression exp1 = operandStack.pop();
      operandStack.push(
          new BinaryOperatorApplication(
              exp1, operatorStack.pop(), exp2));
    } catch (EmptyStackException e) {
      throw new InvalidSyntaxException("Invalid Binary Operator Application");
    }
  }
}