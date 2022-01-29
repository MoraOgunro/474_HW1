import SetTheoryDSL.SetExp.*

import javax.swing.plaf.basic.BasicMenuBarUI
import scala.collection.mutable
import scala.collection.mutable.{Map, Set}
// Value, Variable, Assign, Insert, Delete, Union, Intersection, Difference SymmetricDifference, Cartesian
/*
  Value ::=     input: int => BasicType
  Variable ::=  input: string => Map(input)
  Insert ::=    set: Set, input: BasicType => input
 */

object SetTheoryDSL:
  type BasicType = Any
  val variableBinding: mutable.Map[String, Any] = mutable.Map[String,Any](("Set1" -> mutable.Set(1,2,3)),("Set2" -> mutable.Set(3,4,5)), ("testVar" -> 100))
  val binding2: mutable.Map[String, Any] = mutable.Map[String,Any](("otherSet" -> mutable.Set("dog","cat")), ("testVar" -> 50))
  val scopeMap: mutable.Map[String, Any] = mutable.Map[String,Any](("default" -> variableBinding), ("newScope" -> binding2))
  val currentScopeName: Array[String] = Array("default")
  val macroBindings: mutable.Map[String, SetExp] = mutable.Map[String, SetExp]("mac1" -> Delete(Variable(Value("Set1")), Value(1)))


  enum SetExp:
    case Value(input: BasicType)
    case Variable(name: SetExp)
    case Check(name: SetExp, input: SetExp)
    case Assign(name: SetExp, input: SetExp)
    case Union(set1:SetExp, set2:SetExp)
    case Intersection(set1:SetExp, set2:SetExp)
    case SetDifference(set1:SetExp, set2:SetExp)
    case SymmetricDifference(set1:SetExp, set2:SetExp)
    case Cartesian(set1:SetExp, set2:SetExp)
    case Scope(scopeName: String, expression: SetExp)
    case Macro(name: String, input: SetExp = NoneCase())
    case Delete(name: SetExp, input: SetExp)
    case NoneCase()
    def eval: BasicType =
    this match {
        case Value(i) => i
        /*
          Variable retrieves the bounded value of a variable name.
          Prints error if no such variable exists in variableBinding
        */
        case Variable(name) => {
          val n = name.eval.asInstanceOf[String]
          try {
            (n, (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](n))
          } catch {
            case e: NoSuchElementException => {
              println(s"No variable $n exists within scope ${currentScopeName(0)}")
              return (n, None)}
          }
        }
        /*
         Params:
          name: the Variable Object which holds the Variable name
          input: the object being checked

          returns
              true of object is in the set
              false if object is not in the set
              None if set does not exist
         */
        case Check(name, input) => {
          val result = name.eval.asInstanceOf[Tuple2[String,BasicType]]
          val key = result._1
          val v = result._2
          if(v == None){
            return false
          }
          val valueSet = v.asInstanceOf[mutable.Set[BasicType]]
          val objectToCheck = input.eval
          println(s"Input is: $objectToCheck, Key is: $key, the value is: $valueSet")
          if(objectToCheck != None){
            if(valueSet(objectToCheck)){
              true
            }else{
              false
            }
          }else{
            None
          }
        }
        /*
          Assign
          Locates a set within variableBinding according to the variable name passed
          or creates one if it does not exist.

          Add the input element to the set
        */
        case Assign(name, input) => {
          val variableInfo = name.eval.asInstanceOf[Tuple2[String,BasicType]]
          if((scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]].contains(variableInfo._1)){
            println(s"Found Set with key ${variableInfo._1}")
          }else{
            (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1) = mutable.Set[BasicType]()
            println(s"Did Not Find Set with key ${variableInfo._1}. New Set Created")
          }
          // This is necessary because the user might use a variable or a value.
          // The evaluation of a variable outputs a tuple of objects, but evaluations of Values outputs a singe object
          try {
            (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]] += input.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          }
          catch {
            case e: _ => {
              (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]]  += input.eval
            }
          }
        }

        case Delete(name, input) => {
          val variableInfo = name.eval.asInstanceOf[Tuple2[String,BasicType]]
          if( classOf[mutable.Map[String,Any]].isInstance((scopeMap(currentScopeName(0))))
            && (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]].contains(variableInfo._1)){
            println(s"Deleting Set ${variableInfo._1}")
            // This is necessary because the user might use a variable or a value.
            // The evaluation of a variable outputs a tuple of objects, but evaluations of Values outputs a singe object
            try {
              (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]] -= input.eval.asInstanceOf[Tuple2[String,BasicType]]._2
            }
            catch {
              case e: _ => {
                (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]]  -= input.eval
              }
            }
          }else{
            (scopeMap(currentScopeName(0))).asInstanceOf[mutable.Map[String,Any]](variableInfo._1) = mutable.Set[BasicType]()
            println(s"Did Not Find Set with key ${variableInfo._1}.")
          }
        }
        case Union(set1, set2) => {
          val f = set1.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          val s = set2.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          f.asInstanceOf[mutable.Set[BasicType]] union s.asInstanceOf[mutable.Set[BasicType]]
        }
        case Intersection(set1, set2) => {
          val f = set1.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          val s = set2.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          f.asInstanceOf[mutable.Set[BasicType]] intersect s.asInstanceOf[mutable.Set[BasicType]]
        }
        case SetDifference(set1, set2) => {
          val f = set1.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          val s = set2.eval.asInstanceOf[Tuple2[String,BasicType]]._2
          f.asInstanceOf[mutable.Set[BasicType]] diff s.asInstanceOf[mutable.Set[BasicType]]
        }
        case SymmetricDifference(set1, set2) => {
          Union(set1, set2).eval.asInstanceOf[mutable.Set[BasicType]] diff Intersection(set1, set2).eval.asInstanceOf[mutable.Set[BasicType]]
        }
        case Cartesian(set1,set2) => {
          val f = set1.eval.asInstanceOf[Tuple2[String,BasicType]]._2.asInstanceOf[mutable.Set[BasicType]]
          val s = set2.eval.asInstanceOf[Tuple2[String,BasicType]]._2.asInstanceOf[mutable.Set[BasicType]]
          val cartesian = mutable.Set[BasicType]()
          f.foreach(f_elem => {
            s.foreach(s_elem =>{
              cartesian.addOne((f_elem,s_elem))
            })
          })

          cartesian
        }
        case Scope(scopeName, expression) => {
          currentScopeName(0) = scopeName
          println(currentScopeName.mkString("Array(", ", ", ")"))
          if( !(scopeMap contains currentScopeName(0)) ){
            println(s"scope ${currentScopeName(0)} does not exist, creating it now...")
            scopeMap(currentScopeName(0)) = mutable.Map[String,Any]()
          }
          expression.eval
        }
        case Macro(name, exp: SetExp) => {
          if(exp.eval != None){
            // Then Add this macro to the macro bindings
            macroBindings(name) = exp
          }else{
            //Run this macro
            macroBindings(name).eval
          }
        }
        case NoneCase() =>{
          return None
        }

      }

@main def runSetExp(): Unit =
  // The variable names must be wrapped in Value()
  println(Scope("default", Macro("mac1")).eval)





