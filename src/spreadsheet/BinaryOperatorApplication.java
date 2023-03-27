package spreadsheet;

import static spreadsheet.Associativity.LEFT;
import static spreadsheet.Associativity.RIGHT;

import common.api.CellLocation;
import common.api.EvaluationContext;
import common.api.Expression;
import java.util.Set;

public class BinaryOperatorApplication implements Expression {
  private final Operator operator;
  private final Expression subexp1;
  private final Expression subexp2;

  public BinaryOperatorApplication(Expression exp1, Operator op, Expression exp2) {
    subexp1 = exp1;
    operator = op;
    subexp2 = exp2;
  }

  @Override
  public double evaluate(EvaluationContext context) {
    return operator.apply(subexp1.evaluate(context), subexp2.evaluate(context));
  }

  @Override
  public void findCellReferences(Set<CellLocation> dependencies) {
    subexp1.findCellReferences(dependencies);
    subexp2.findCellReferences(dependencies);
  }

  @Override
  public String toString() {
    return String.format("(%s %s %s)", subexp1.toString(), operator.toString(), subexp2.toString());
  }

  public enum Operator implements Operation {
    ADD(1, LEFT, "+", Double::sum),
    SUB(1, LEFT, "-", (a, b) -> a - b),
    MUL(2, LEFT, "*", (a, b) -> a * b),
    DIV(2, LEFT, "/", (a, b) -> a / b),
    POW(3, RIGHT, "^", Math::pow),
    LPAREN(0, RIGHT, "(", null),
    RPAREN(0, LEFT, ")", null);

    public final int precedence;
    public final Associativity associativity;
    public final String operator;
    private final Operation operation;

    Operator(int p, Associativity ass, String op, Operation o) {
      precedence = p;
      associativity = ass;
      operator = op;
      operation = o;
    }

    @Override
    public double apply(double a, double b) {
      return operation.apply(a, b);
    }

    public int getPrecedence() {
      return precedence;
    }

    public Associativity getAssociativity() {
      return associativity;
    }

    @Override
    public String toString() {
      return operator;
    }
  }

  public interface Operation {

    double apply(double a, double b);
  }
}
