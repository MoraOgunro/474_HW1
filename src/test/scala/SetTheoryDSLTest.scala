import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import SetTheoryDSL.SetExp.*

import scala.collection.mutable

class SetTheoryDSLTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  behavior of "my set theory DSL"
  before {
    Scope("default", Assign(Variable(Value("firstSet")), Value(5))).eval
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
  "Variable" should "return a tuple containing information about a Variable or return (name, None)" in {
    Scope("default", Variable(Value("missingSet"))).eval should be ("missingSet",None)
  }

  "Check" should "test if the given input exists in a set" in {
    assert(Check(Variable(Value("firstSet")), Value(5)).eval === true)
  }
}

