import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SetTheoryDSL.SetExp.*

import scala.collection.mutable

class SetTheoryDSLTest extends AnyFlatSpec with Matchers {
  behavior of "my set theory DSL"
  val myDSL: SetTheoryDSL.SetExp.type = SetTheoryDSL.SetExp
  "Value" should "return the same value that was passed" in {
    Scope("default", Value(Int.MinValue)).eval should be (Int.MinValue)
    Scope("default", Value(Int.MaxValue)).eval should be (Int.MaxValue)
    Scope("default", Value(0)).eval should be (0)
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
    a [IllegalArgumentException] should be thrownBy {
      Scope("default", Assign(Value("testSet"), Value(10))).eval
    }
  }
}

