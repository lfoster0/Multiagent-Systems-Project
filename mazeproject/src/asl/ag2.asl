// Agent ag1 in project mazeproject

/* Initial beliefs and rules */


/* Initial goals */

!explore.

/* Plans */
+!explore : at(X,Y,goal2) <- pickup(X,Y); !returnToStart.

+!explore : left(X,Y,goal2) <- move(X,Y); !explore.
+!explore : right(X,Y,goal2) <- move(X,Y); !explore.
+!explore : top(X,Y,goal2) <- move(X,Y); !explore.
+!explore : down(X,Y,goal2) <- move(X,Y); !explore.

+!explore : true  <- moveRandom; !explore.

// Record I saw goal0
+left(X,Y,goal0) : not seenGoal(X,Y,goal0) <- recordSeeingGoal(goal0,X,Y); .print("I've seen goal 0 at ", X, " ", Y).
+right(X,Y,goal0) : not seenGoal(X,Y,goal0) <- recordSeeingGoal(goal0,X,Y); .print("I've seen goal 0 at ", X, " ", Y).
+top(X,Y,goal0) : not seenGoal(X,Y,goal0) <- recordSeeingGoal(goal0,X,Y); .print("I've seen goal 0 at ", X, " ", Y).
+down(X,Y,goal0) : not seenGoal(X,Y,goal0) <- recordSeeingGoal(goal0,X,Y); .print("I've seen goal 0 at ", X, " ", Y).

// Record I saw goal1
+left(X,Y,goal1) : not seenGoal(X,Y,goal1) <- recordSeeingGoal(goal1,X,Y); .print("I've seen goal 1 at ", X, " ", Y).
+right(X,Y,goal1) : not seenGoal(X,Y,goal1) <- recordSeeingGoal(goal1,X,Y); .print("I've seen goal 1 at ", X, " ", Y).
+top(X,Y,goal1) : not seenGoal(X,Y,goal1) <- recordSeeingGoal(goal1,X,Y); .print("I've seen goal 1 at ", X, " ", Y).
+down(X,Y,goal1) : not seenGoal(X,Y,goal1) <- recordSeeingGoal(goal1,X,Y); .print("I've seen goal 1 at ", X, " ", Y).

// Record I saw goal3
+left(X,Y,goal3) : not seenGoal(X,Y,goal3) <- recordSeeingGoal(goal3,X,Y); .print("I've seen goal 3 at ", X, " ", Y).
+right(X,Y,goal3) : not seenGoal(X,Y,goal3) <- recordSeeingGoal(goal3,X,Y); .print("I've seen goal 3 at ", X, " ", Y).
+top(X,Y,goal3) : not seenGoal(X,Y,goal3) <- recordSeeingGoal(goal3,X,Y); .print("I've seen goal 3 at ", X, " ", Y).
+down(X,Y,goal3) : not seenGoal(X,Y,goal3) <- recordSeeingGoal(goal3,X,Y); .print("I've seen goal 3 at ", X, " ", Y).

/* Return to start */
+!returnToStart: at(X,Y,start) <- !done.
+!returnToStart: true <- moveBack; ?at(X,Y,SPACE); !returnToStart.

/* Done */
+!done: true <- .print("done").

+blockedFor(T) : T>5 & top(X,Y,AG) & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .print("Asking ", AG, " to Move"); .send(AG, achieve, moveOver).
+blockedFor(T) : T>5 & down(X,Y, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .print("Asking ", AG, " to Move"); .send(AG, achieve, moveOver).
+blockedFor(T) : T>5 & right(X,Y, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .print("Asking ", AG, " to Move"); .send(AG, achieve, moveOver).
+blockedFor(T) : T>5 & left(X,Y, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .print("Asking ", AG, " to Move"); .send(AG, achieve, moveOver).

// At the start and there is a free space
+!moveOver: at(X,Y,start) & top(X2,Y2,start) <- move(X2,Y2); .print("I moved!").
+!moveOver: at(X,Y,start) & left(X2,Y2,start) <- move(X2,Y2); .print("I moved!").
+!moveOver: at(X,Y,start) & right(X2,Y2,start) <- move(X2,Y2); .print("I moved!").
+!moveOver: at(X,Y,start) & down(X2,Y2,start) <- move(X2,Y2); .print("I moved!").

// At the start and I have to ask another agent to move
+!moveOver: at(X,Y,start) & top(X2,Y2,AG) & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .send(AG, achieve, moveOver); move(X2,Y2).
+!moveOver: at(X,Y,start) & down(X2,Y2, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .send(AG, achieve, moveOver); move(X2,Y2).
+!moveOver: at(X,Y,start) & right(X2,Y2, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .send(AG, achieve, moveOver); move(X2,Y2).
+!moveOver: at(X,Y,start) & left(X2,Y2, AG)  & (AG = ag0 | AG = ag1 | AG = ag2 | AG = ag3) <- .send(AG, achieve, moveOver); move(X2,Y2).

// Not at start and asked to move
+!moveOver: top(X,Y,empty) <- move(X,Y).
+!moveOver: down(X,Y,empty) <- move(X,Y).
+!moveOver: right(X,Y,empty) <- move(X,Y).
+!moveOver: left(X,Y,empty) <- move(X,Y).

/****************************************************************************************************************/
/* All the logic for communicating paths to the other goals and following paths once the agent has received one */
/****************************************************************************************************************/
// Tell an agent it knows where its goal is
+left(X,Y,ag0) : seenGoal(X2,Y2, goal0) & not givenPathToGoal(goal0) <- .send(ag0,unachieve,explore); .send(ag0, tell, getPathFrom(ag2)).
+right(X,Y,ag0) : seenGoal(X2,Y2, goal0) & not givenPathToGoal(goal0) <-.send(ag0,unachieve,explore); .send(ag0, tell, getPathFrom(ag2)).
+top(X,Y,ag0) : seenGoal(X2,Y2, goal0) & not givenPathToGoal(goal0) <- .send(ag0,unachieve,explore); .send(ag0, tell, getPathFrom(ag2)).
+down(X,Y,ag0) : seenGoal(X2,Y2, goal0) & not givenPathToGoal(goal0) <- .send(ag0,unachieve,explore); .send(ag0, tell, getPathFrom(ag2)).

+left(X,Y,ag1) : seenGoal(X2,Y2, goal1) & not givenPathToGoal(goal1) <- .send(ag1,unachieve,explore); .send(ag1, tell, getPathFrom(ag2)).
+right(X,Y,ag1) : seenGoal(X2,Y2, goal1) & not givenPathToGoal(goal1) <-.send(ag1,unachieve,explore); .send(ag1, tell, getPathFrom(ag2)).
+top(X,Y,ag1) : seenGoal(X2,Y2, goal1) & not givenPathToGoal(goal1) <- .send(ag1,unachieve,explore); .send(ag1, tell, getPathFrom(ag2)).
+down(X,Y,ag1) : seenGoal(X2,Y2, goal1) & not givenPathToGoal(goal1) <- .send(ag1,unachieve,explore); .send(ag1, tell, getPathFrom(ag2)).

+left(X,Y,ag3) : seenGoal(X2,Y2, goal3) & not givenPathToGoal(goal3) <- .send(ag3,unachieve,explore); .send(ag3, tell, getPathFrom(ag2)).
+right(X,Y,ag3) : seenGoal(X2,Y2, goal3) & not givenPathToGoal(goal3) <- .send(ag3,unachieve,explore); .send(ag3, tell, getPathFrom(ag2)).
+top(X,Y,ag3) : seenGoal(X2,Y2, goal3) & not givenPathToGoal(goal3) <- .send(ag3,unachieve,explore); .send(ag3, tell, getPathFrom(ag2)).
+down(X,Y,ag3) : seenGoal(X2,Y2, goal3) & not givenPathToGoal(goal3) <- .send(ag3,unachieve,explore); .send(ag3, tell, getPathFrom(ag2)).

// Get the path from the agent and save it to its java object representation
+getPathFrom(AG) : not followingPathToGoal & not hasGoal  <- getPathFromAgent(AG); !followPathToGoal.

// Follow to the goal until we're there
+!followPathToGoal : at(X,Y,goal2) <- pickup(X,Y); !returnToStart.
+!followPathToGoal : followingPathToGoal <- followPath; !followPathToGoal.
+!followPathToGoal : true <- followPath; !explore.