package org.enso.interpreter.node.expression.builtin.bool;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.profiles.ConditionProfile;
import org.enso.interpreter.dsl.BuiltinMethod;
import org.enso.interpreter.dsl.Suspend;
import org.enso.interpreter.node.BaseNode;
import org.enso.interpreter.node.callable.thunk.ThunkExecutorNode;
import org.enso.interpreter.runtime.state.State;

@BuiltinMethod(
    type = "Boolean",
    name = "||",
    description = "Computes the logical disjunction of two booleans")
public abstract class OrNode extends Node {

  private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();

  public static OrNode build() {
    return OrNodeGen.create();
  }

  abstract Object execute(State state, boolean self, @Suspend Object that);

  @Specialization
  Object executeBool(
      State state,
      boolean self,
      Object that,
      @Cached("build()") ThunkExecutorNode rhsThunkExecutorNode) {
    if (conditionProfile.profile(self)) {
      return true;
    }
    return rhsThunkExecutorNode.executeThunk(that, state, BaseNode.TailStatus.TAIL_DIRECT);
  }
}
