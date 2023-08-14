## SWEN301 Assignment 1 Template

my design doesnt explicitly close everything at the end of runtime and each individual method. The reset() method is only called after tests so this makes it prone to memory leaks.
also i am not using connection pools to manage connections which results in my design not reusing connections which could lead to memory problems.
 I may not be removing everything from cache properly which would also result in memory problems.

