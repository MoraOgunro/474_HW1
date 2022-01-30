# Mora Ogunro
CS474 Homework 1

# How to run the program
1. Download the files and extract them
2. navigate to 474_HW1_SBT/src/main/scala and open SetTheoryDSL.scala in a text editor of your choice
3. open up a terminal or command line and navigate to the project folder within it
4. enter the sbt shell by running the command "sbt" in the terminal
5. run the command "compile" to compile the project files
6. run the "run" command to run the project
7. If successful, you should see a welcome message printed in the sbt shell

To use this language, insert expressions within the method body of "runSetExp()", which is located in SetTheoryDSL.scala. \
After making changes to the file, **save and complete steps 5 and 6 again**. You must do this after every change.

See the **SYNTAX** section below for descriptions of each procedure and how to use them.
# How to run the tests
In the sbt shell, run the command "test"

# Syntax
**Value(input: BasicType)** \
**Variable(name: SetExp)** \
**Check(name: SetExp, input: SetExp)** \
**Assign(name: SetExp, input: SetExp)** \
**Union(set1:SetExp, set2:SetExp)** \
**Intersection(set1:SetExp, set2:SetExp)** \
**SetDifference(set1:SetExp, set2:SetExp)** \
**SymmetricDifference(set1:SetExp, set2:SetExp)** \
**Cartesian(set1:SetExp, set2:SetExp)** \
**Scope(scopeName: String, expression: SetExp)** \
**Macro(name: String, input: SetExp = NoneCase())** \
**Delete(name: SetExp, input: SetExp)** \
**NoneCase()** \
Used by the language.

# Troubleshooting
If sbt cannot create a local server, close sbt shell and intelliJ if it is open. Then reopen sbt shell and run the "compile" command. \
The sbt shell in IntelliJ will sometimes conflict with the sbt shell in your personal terminal.

