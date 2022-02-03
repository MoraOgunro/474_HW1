import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import SetTheoryDSL.SetExp.*

import scala.collection.mutable

class SetTheoryDSLTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  behavior of "my set theory DSL"
  before {
    Scope("default", Assign(Variable(Value("firstSet")), Value(1))).eval
    Scope("default", Assign(Variable(Value("firstSet")), Value(2))).eval
    Scope("default", Assign(Variable(Value("firstSet")), Value(5))).eval

    Scope("default", Assign(Variable(Value("secondSet")), Value(5))).eval
    Scope("default", Assign(Variable(Value("secondSet")), Value(6))).eval
    Scope("default", Assign(Variable(Value("secondSet")), Value(7))).eval
  }
  "Value" should "return the same value that was passed" in {
    Scope("default", Value(Int.MinValue)).eval should be (Int.MinValue)
    Scope("default", Value(Int.MaxValue)).eval should be (Int.MaxValue)
    Scope("default", Value(9)).eval should be (9)
  }
  "Assign" should "create a set if the specified set is not found" in {
    Scope("default", Assign(Variable(Value("testSet")), Value(5))).eval
    Scope("default", Variable(Value("testSet"))).eval should be ("testSet",mutable.HashSet(5))
  }
  it should "insert an object into a set" in {
    Scope("default", Assign(Variable(Value("testSet")), Value(10))).eval
    Scope("default", Variable(Value("testSet"))).eval should be ("testSet",mutable.HashSet(5,10))
  }
  it should "throw IllegalArgumentException if the first parameter is not a variable object" in {
    a[IllegalArgumentException] should be thrownBy {
      Scope("default", Assign(Value("testSet"), Value(10))).eval
    }
  }
  "Delete" should "remove an element from a set" in {
    Delete(Variable(Value("firstSet")), Value(5)).eval
    Variable(Value("firstSet")).eval should be ("firstSet", mutable.Set(1,2))
  }
  "Variable" should "return a tuple containing information about a Variable or return (name, None)" in {
    Scope("default", Variable(Value("missingSet"))).eval should be ("missingSet",None)
  }

  "Check" should "test if the given input exists in a set" in {
    assert(Check(Variable(Value("firstSet")), Value(5)).eval === true)
  }
  "Union" should "return the union of two sets" in {
    assert( Scope("default", Union(Variable(Value("firstSet")), Variable(Value("secondSet")))).eval === mutable.Set(1,2,5,6,7))
  }
  "Intersection" should "return the intersection of two sets" in {
    assert( Scope("default", Intersection(Variable(Value("firstSet")), Variable(Value("secondSet")))).eval === mutable.Set(5))

  }
  "SetDifference" should "return the SetDifference of two sets" in {
    assert( Scope("default", SetDifference(Variable(Value("firstSet")), Variable(Value("secondSet")))).eval === mutable.Set(1,2))
  }
  "SymmetricDifference" should "return the Cartesian of two sets" in {
    assert( Scope("default", SymmetricDifference(Variable(Value("firstSet")), Variable(Value("secondSet")))).eval === mutable.Set(1,2,6,7))
  }
  "Cartesian" should "return the Cartesian of two sets" in {
    assert( Scope("default", Cartesian(Variable(Value("firstSet")), Variable(Value("secondSet")))).eval === mutable.Set((1,6), (2,5), (5,7), (5,5), (2,6), (5,6), (2,7), (1,7), (1,5)))
  }
  "Macro" should "Create a macro" in {
    Macro("myMacro", Delete(Variable(Value("firstSet")), Value(1))).eval
    Scope("default", Macro("myMacro")).eval
    val result: (String,Any) = Scope("default", Variable(Value("firstSet"))).eval.asInstanceOf[(String,Any)]
    assert( result._2 === mutable.Set(2,5))
  }
}

