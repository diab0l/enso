package org.enso.compiler;

import com.oracle.truffle.api.source.Source;

import java.io.IOException;

import org.enso.compiler.core.IR;
import org.enso.compiler.core.IR$Error$Syntax;
import org.enso.compiler.core.IR$Error$Syntax$InvalidEscapeSequence$;
import org.enso.compiler.core.IR$Error$Syntax$Reason;
import org.enso.compiler.core.IR$Error$Syntax$InvalidImport$;
import org.enso.compiler.core.IR$Error$Syntax$UnexpectedExpression$;
import org.enso.compiler.core.IR$Error$Syntax$UnrecognizedToken$;
import org.enso.compiler.core.IR$Error$Syntax$UnsupportedSyntax;
import org.enso.syntax.text.Location;

import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.collection.immutable.List;

public class ErrorCompilerTest {
  private static EnsoCompiler ensoCompiler;

  @BeforeClass
  public static void initEnsoCompiler() {
    ensoCompiler = new EnsoCompiler();
  }

  @AfterClass
  public static void closeEnsoCompiler() throws Exception {
    ensoCompiler.close();
  }

  @Test
  public void spaceRequired() throws Exception {
    var ir = parseTest("foo = if cond.x else.y");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 8);
  }

  @Test
  public void incompleteTypeDefinition() throws Exception {
    var ir = parseTest("type");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 4);
  }

  @Test
  public void badCase1() throws Exception {
    var ir = parseTest("""
    foo = case x of
     4
    """);
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 18);
  }

  @Test
  public void badCase2() throws Exception {
    var ir = parseTest("""
    foo = case x of
     4 ->
    """);
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 21);
  }

  @Test
  public void badCase3() throws Exception {
    var ir = parseTest("""
    foo = case x of
     4->
    """);
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 20);
  }

  @Test
  public void malformedSequence1() throws Exception {
    var ir = parseTest("(1, )");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 5);
  }

  @Test
  public void malformedSequence2() throws Exception {
    var ir = parseTest("foo = (1, )");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 7, 9);
  }

  @Test
  public void unmatchedDemiliter1() throws Exception {
    var ir = parseTest("(");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 1);
  }

  @Test
  public void unmatchedDemiliter2() throws Exception {
    var ir = parseTest(")");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 1);
  }

  @Test
  public void unmatchedDemiliter3() throws Exception {
    var ir = parseTest("[");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 1);
  }

  @Test
  public void unmatchedDemiliter4() throws Exception {
    var ir = parseTest("[");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 1);
  }

  @Test
  public void unmatchedDemiliter5() throws Exception {
    var ir = parseTest("foo = (");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 7);
  }

  @Test
  public void unmatchedDemiliter6() throws Exception {
    var ir = parseTest("foo = )");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 7);
  }

  @Test
  public void unmatchedDemiliter7() throws Exception {
    var ir = parseTest("foo = [");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 7);
  }

  @Test
  public void unmatchedDemiliter8() throws Exception {
    var ir = parseTest("foo = ]");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 7);
  }

  @Test
  public void unexpectedSpecialOperator() throws Exception {
    var ir = parseTest("foo = 1, 2");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 6, 10);
  }

  @Test
  public void malformedImport1() throws Exception {
    var ir = parseTest("import");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 6);
  }

  @Test
  public void malformedImport2() throws Exception {
    var ir = parseTest("import as Foo");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 13);
  }

  @Test
  public void malformedImport3() throws Exception {
    var ir = parseTest("import Foo as Foo, Bar");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 22);
  }

  @Test
  public void malformedImport4() throws Exception {
    var ir = parseTest("import Foo as Foo.Bar");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 21);
  }

  @Test
  public void malformedImport5() throws Exception {
    var ir = parseTest("import Foo as");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 13, 13);
  }

  @Test
  public void malformedImport6() throws Exception {
    var ir = parseTest("import Foo as Bar.Baz");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 21);
  }

  @Test
  public void malformedImport7() throws Exception {
    var ir = parseTest("import Foo hiding");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 17, 17);
  }

  @Test
  public void malformedImport8() throws Exception {
    var ir = parseTest("import Foo hiding X,");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 18, 20);
  }

  @Test
  public void malformedImport9() throws Exception {
    var ir = parseTest("polyglot import Foo");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnrecognizedToken$.MODULE$, "Unrecognized token.", 0, 19);
  }

  @Test
  public void malformedImport10() throws Exception {
    var ir = parseTest("polyglot java import");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 20);
  }

  @Test
  public void malformedImport11() throws Exception {
    var ir = parseTest("from import all");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 4, 4);
  }

  @Test
  public void malformedImport12() throws Exception {
    var ir = parseTest("from Foo import all hiding");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 26, 26);
  }

  @Test
  public void malformedImport13() throws Exception {
    var ir = parseTest("from Foo import all hiding X.Y");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 27, 30);
  }

  @Test
  public void malformedExport1() throws Exception {
    var ir = parseTest("export");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 6);
  }

  @Test
  public void malformedExport2() throws Exception {
    var ir = parseTest("export as Foo");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 13);
  }

  @Test
  public void malformedExport3() throws Exception {
    var ir = parseTest("export Foo as Foo, Bar");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 22);
  }

  @Test
  public void malformedExport4() throws Exception {
    var ir = parseTest("export Foo as Foo.Bar");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 21);
  }

  @Test
  public void malformedExport5() throws Exception {
    var ir = parseTest("export Foo as");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 13, 13);
  }

  @Test
  public void malformedExport6() throws Exception {
    var ir = parseTest("export Foo as Bar.Baz");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 14, 21);
  }

  @Test
  public void malformedExport7() throws Exception {
    var ir = parseTest("export Foo hiding");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 7, 17);
  }

  @Test
  public void malformedExport8() throws Exception {
    var ir = parseTest("export Foo hiding X,");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 7, 20);
  }

  @Test
  public void malformedExport9() throws Exception {
    var ir = parseTest("from export all");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 4, 4);
  }

  @Test
  public void malformedExport10() throws Exception {
    var ir = parseTest("from Foo export all hiding");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 26, 26);
  }

  @Test
  public void malformedExport11() throws Exception {
    var ir = parseTest("from Foo export all hiding X.Y");
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidImport$.MODULE$, "Imports must have a valid module path.", 27, 30);
  }

  @Test
  public void invalidToken1() throws Exception {
    var ir = parseTest("`");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 1);
  }

  @Test
  public void invalidToken2() throws Exception {
    var ir = parseTest("splice_outside_text = `");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 22, 23);
  }

  @Test
  public void illegalForeignBody1() throws Exception {
    var ir = parseTest("foreign 4");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 9);
  }

  @Test
  public void illegalForeignBody2() throws Exception {
    var ir = parseTest("foreign 4 * 4");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 13);
  }

  @Test
  public void illegalForeignBody3() throws Exception {
    var ir = parseTest("foreign foo = \"4\"");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 17);
  }

  @Test
  public void illegalForeignBody4() throws Exception {
    var ir = parseTest("foreign js foo = 4");
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 0, 18);
  }

  @Test
  public void illegalEscapeSequence() throws Exception {
    var ir = parseTest("""
    escape = 'wrong \\c sequence'
    """);
    assertSingleSyntaxError(ir, IR$Error$Syntax$InvalidEscapeSequence$.MODULE$.apply("wrong  sequence"), "Invalid escape sequence wrong  sequence.", 9, 28);
  }

  @Test
  public void testNPE183814303() throws Exception {
    var ir = parseTest("""
    from Standard.Base import all

    main =
        x = "foo"
        z = x. length
        IO.println z
    """);
    assertSingleSyntaxError(ir, IR$Error$Syntax$UnexpectedExpression$.MODULE$, "Unexpected expression.", 60, 62);
  }

  @Test
  public void testNPE183863754() throws Exception {
    var ir = parseTest("""
    main =
    #    meh
         42
    """);
    var errors = ir.preorder().filter(IR$Error$Syntax.class::isInstance).map(IR$Error$Syntax.class::cast);
    assertEquals("Two errors", 2, errors.size());
    assertTrue(errors.head().reason() instanceof IR$Error$Syntax$UnsupportedSyntax);
    assertEquals(errors.head().location().get().start(), 0);
    assertEquals(errors.head().location().get().length(), 6);
  }

  private void assertSingleSyntaxError(
      IR.Module ir, IR$Error$Syntax$Reason type,
      String msg, int start, int end
  ) {
    var errors = assertIR(ir, IR$Error$Syntax.class, 1);
    assertEquals(type, errors.head().reason());
    assertEquals(msg, errors.head().message());
    assertEquals(new Location(start, end), errors.head().location().get().location());
  }

  private List<IR$Error$Syntax> assertIR(IR.Module ir, Class<IR$Error$Syntax> type, int count) {
    var errors = ir.preorder().filter(type::isInstance).map(type::cast);
    assertEquals("Expecting errors: " + errors, count, errors.size());
    return errors;
  }

  private static IR.Module parseTest(String code) throws IOException {
    var src =
        Source.newBuilder("enso", code, "test-" + Integer.toHexString(code.hashCode()) + ".enso")
            .build();
    var ir = ensoCompiler.compile(src);
    assertNotNull("IR was generated", ir);
    return ir;
  }
}