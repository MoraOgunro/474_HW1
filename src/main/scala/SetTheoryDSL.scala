import SetTheoryDSL.SetExp.*

import javax.swing.plaf.basic.BasicMenuBarUI
import scala.collection.mutable
import scala.collection.mutable.{Map, Set}

/** SetTheoryDSL provides a set theory language for the user to perform actions on sets */
object SetTheoryDSL:
  type BasicType = Any
  /** variableBinding is the default scope. */
  val variableBinding: mutable.Map[String, Any] = mutable.Map[String,Any]("Set1" -> mutable.Set(1,2,3), "Set2" -> mutable.Set(3,4,5), "testVar" -> 100)
  /** TODO: REMOVE */
  val binding2: mutable.Map[String, Any] = mutable.Map[String,Any]("otherSet" -> mutable.Set("dog","cat"), "testVar" -> 50)
  /** scopeMap is a collection of variable scopes*/
  val scopeMap: mutable.Map[String, Any] = mutable.Map[String,Any]("default" -> variableBinding, "newScope" -> binding2)
  /** the scope that is currently active */
  val currentScopeName: Array[String] = Array("default")
  /** a map of user-defined macro commands */
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

      /** Returns the value that was passed into it
       *
       *  param i a primitive value
       *  return the value that was passed into it
       */
        case Value(i) => i

        /** Retrieves the value associated with a variable name from the scopeMap
         *
         *  param name the variable name
         *  return a tuple containing the variable name and its value. The value will be None if
         *    the variable does not exist.
         */
        case Variable(name) =>
          /** name is of type SetExp, it must be converted into a string */
          val n = name.eval.asInstanceOf[String]
          try {
            /** using currentScopeName to retrieve the appropriate variable bindings
             * scopeMap returns a map of variables, which is used to find the value of variable n
             */
            (n, scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]](n))
          } catch {
            /**
             * The variable n does not exist. Return a tuple containing the variable name and None
             */
            case e: NoSuchElementException =>
              println(s"No variable $n exists within scope ${currentScopeName(0)}")
              (n, None)
          }

        /** Tests if the given input exists within the specified set
         *
         *  param name the variable name of the set
         *  param input the object to be tested
         *  return true if the input exists in a set and false if not.
         */
        case Check(name, input) =>
          /** name is a variable. Retrieve the evaluation of this variable, which is a tuple. */
          val result = name.eval.asInstanceOf[(String, BasicType)]
          val key = result._1
          val v = result._2

          /** the set does not exist */
          if(v == None){
            println("That set does not exist.")
            return false
          }
          /** the set exists and must be recognized as an instance of a mutable set */
          val valueSet = v.asInstanceOf[mutable.Set[BasicType]]
          val objectToCheck = input.eval

          println(s"Checking if $objectToCheck exists in the set $key, which contains $valueSet")
          if(objectToCheck != None){
            if(valueSet(objectToCheck)){
              true
            }else{
              false
            }
          }else{
            /** if the input was None for any reason, return None instead of false
             * and allow the calling function interpret the meaning. */
            println(s"$objectToCheck is None.")
            None
          }

        /** Inserts an object into a set. Creates the set if none exists.
         *
         *  param name the name of the set
         *  param input the object to be inserted
         *  return nothing.
         */
        case Assign(name, input) =>
          /** the user must call Assign on a variable, not a string literal */
          if(!name.isInstanceOf[Variable]){
            println("Assign must be called on a Variable name, not a string literal.\n" +
              "Try using Assign(Variable(Value(variableName))).\nRemember that the variable does not have to exist, as Assign will create one for you." +
              "Refer to the syntax documentation for an example.")
            throw new IllegalArgumentException
          }
          val scope = scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]]
          /** variable info contains the tuple evaluation of the name variable */
          val variableInfo = name.eval.asInstanceOf[(String, BasicType)]

          /** create the set if it does not exist with the current scope */
          if(scope.contains(variableInfo._1)){
            println(s"Found Set with key ${variableInfo._1}")
          }else{
            scope(variableInfo._1) = mutable.Set[BasicType]()
            println(s"Did Not Find Set with key ${variableInfo._1}. New Set Created")
          }

          /**
           * The evaluation of a variable outputs a tuple of objects, but evaluations of Values outputs a singe object
           * both cases need to eb accounted for.
           * */
          try {
            scope(variableInfo._1).asInstanceOf[mutable.Set[BasicType]] += input.eval.asInstanceOf[(String, BasicType)]._2
          }
          catch {
            case e: _ =>
              scope(variableInfo._1).asInstanceOf[mutable.Set[BasicType]]  += input.eval
          }
          println(s"Object inserted. The set now contains: ${scope(variableInfo._1).asInstanceOf[mutable.Set[BasicType]]}")

        /** Deletes an object from a set
         *
         *  param name the variable name of the set
         *  param input the object to be deleted
         *  return nothing
         */
        case Delete(name, input) =>
          /** retrieve the tuple returned by Variable() */
          val variableInfo = name.eval.asInstanceOf[(String, BasicType)]
          /** if the requested scope exists and it contains the specified set */
          if( classOf[mutable.Map[String,Any]].isInstance(scopeMap(currentScopeName(0)))
            && scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]].contains(variableInfo._1)){
            println(s"Deleting from set ${variableInfo._1}")
            // This is necessary because the user might use a variable or a value.
            // The evaluation of a variable outputs a tuple of objects, but evaluations of Values outputs a singe object
            try {
              scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]] -= input.eval.asInstanceOf[(String, BasicType)]._2
            }
            catch {
              case e: _ =>
                scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]](variableInfo._1).asInstanceOf[mutable.Set[BasicType]]  -= input.eval
            }
          }else{
            scopeMap(currentScopeName(0)).asInstanceOf[mutable.Map[String,Any]](variableInfo._1) = mutable.Set[BasicType]()
            println(s"Did Not Find Set with key ${variableInfo._1}. Nothing was deleted.")
          }

        /** The union of two sets
         *
         *  param set1 a set
         *  param set2 a set
         *  return the union of set1 and set2 as type mutable.Set
         */
        case Union(set1, set2) =>
          /** retrieve the two sets from their variable bindings */
          val f = set1.eval.asInstanceOf[(String, BasicType)]._2
          val s = set2.eval.asInstanceOf[(String, BasicType)]._2
          /** set1 and set2 must be recognized as type Set */
          f.asInstanceOf[mutable.Set[BasicType]] union s.asInstanceOf[mutable.Set[BasicType]]

        /** The intersection of two sets
         *
         *  param set1 a set
         *  param set2 a set
         *  return the intersection of set1 and set2 as type mutable.Set
         */
        case Intersection(set1, set2) =>
          /** retrieve the two sets from their variable bindings */
          val f = set1.eval.asInstanceOf[(String, BasicType)]._2
          val s = set2.eval.asInstanceOf[(String, BasicType)]._2
          /** set1 and set2 must be recognized as type Set */
          f.asInstanceOf[mutable.Set[BasicType]] intersect s.asInstanceOf[mutable.Set[BasicType]]

        /** The set difference of two sets
         *
         *  param set1 a set
         *  param set2 a set
         *  return the difference of set1 and set2 as type mutable.Set
         */
        case SetDifference(set1, set2) =>
          /** retrieve the two sets from their variable bindings */
          val f = set1.eval.asInstanceOf[(String, BasicType)]._2
          val s = set2.eval.asInstanceOf[(String, BasicType)]._2
          /** set1 and set2 must be recognized as type Set */
          f.asInstanceOf[mutable.Set[BasicType]] diff s.asInstanceOf[mutable.Set[BasicType]]

        /** The symmetric difference of two sets
         *
         *  param set1 a set
         *  param set2 a set
         *  return the symmetric of set1 and set2 as type mutable.Set
         */
        case SymmetricDifference(set1, set2) =>
          /** set1 and set2 must be recognized as type Set */
          /** Symmetric difference is the difference between the union and intersection */
          Union(set1, set2).eval.asInstanceOf[mutable.Set[BasicType]] diff Intersection(set1, set2).eval.asInstanceOf[mutable.Set[BasicType]]

        /** The cartesian product of two sets
         *
         *  param set1 a set
         *  param set2 a set
         *  return the cartesian product of set1 and set2 as type mutable.Set
         */
        case Cartesian(set1,set2) =>
          /** retrieve the two sets from their variable bindings */
          val f = set1.eval.asInstanceOf[(String, BasicType)]._2.asInstanceOf[mutable.Set[BasicType]]
          val s = set2.eval.asInstanceOf[(String, BasicType)]._2.asInstanceOf[mutable.Set[BasicType]]
          val cartesian = mutable.Set[BasicType]()
          /** n squared loop through f and s */
          f.foreach(f_elem => {
            s.foreach(s_elem =>{
              cartesian.addOne((f_elem,s_elem))
            })
          })

          cartesian

        /** Sets the current scope and creates on if it does not exist
         *
         *  param scopeName the variable name of the scope
         *  param expression the SetExp to be evaluated within scope
         *  return the evaluated expression
         */
        case Scope(scopeName, expression) =>
          /** set currentScopeName to scopeName */
          currentScopeName(0) = scopeName
          /** if the scope does not exist, create it */
          if( !(scopeMap contains currentScopeName(0)) ){
            println(s"Scope ${currentScopeName(0)} does not exist, creating it now...")
            scopeMap(currentScopeName(0)) = mutable.Map[String,Any]()
          }
          expression.eval

        /** Creates a macro binding with a given name and expression
         *
         *  param name the variable name of the macro
         *  param exp the expression to be saved as a macro
         *  return nothing
         */
        case Macro(name, exp: SetExp) =>
          if(exp.eval != None){
            // Then Add this macro to the macro bindings
            macroBindings(name) = exp
          }else{
            //Run this macro
            macroBindings(name).eval
          }

        /** NoneCase case used by various expressions
         *
         *  return None
         */
        case NoneCase() =>
          println("None Case Found")
          None

    }

@main def runSetExp(): Unit =
  println("***Welcome to my Set Theory DSL!***")
  // Place your expressions here. View README.md for syntax documentation
  println(Scope("yoyo", Assign(Variable(Value("burger")), Value(10))).eval)
  ()





