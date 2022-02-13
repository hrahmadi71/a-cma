---
layout: default
title: Introduction
nav_order: 1
description: "ACMA smart agents are three agents built with Deep Q-learning method upon the old ACMA tool to refactor software systems"
permalink: /
---

# What is ACMA smart agents?
{: .fs-9 }

ACMA smart agents are three agents built with Deep Q-learning method upon tha old ACMA tool to refactor software systems. They contain two tools which must be run together: 1) custom ACMA tool and 2) its DQN Server.
{: .fs-6 .fw-300 }

[View ACMA on GitHub](https://github.com/hrahmadi71/a-cma){: .btn .bg-red-000 .text-grey-lt-000 .fs-5 .mb-4 .mb-md-0 .mr-2 } [View DQN Server on GitHub](https://github.com/hrahmadi71/acma_dqn_server){: .btn .fs-5 .mb-4 .mb-md-0 }

---

## Introduction

### History
[A-CMA](https://github.com/hrahmadi71/a-cma/) refactors Java projects by receiving their Java byte-code as input. This tool first gets the Java byte-code and then extracts its structure (i.e. classes, methods, fields, relations, method-inputs, access levels, etc.). A-CMA can measure several metrics as well as the 26 metrics which we have used to represent the state shown in the table below.


|     Measure-ID      |     Description                                                                                                 |
|---------------------|-----------------------------------------------------------------------------------------------------------------|
| numFields           |     The number of fields   per class.                                                                           |
| avrgFieldVisibility |     The   average value of field visibility per   class (where private has the lowest and public has the highest values).|
| numConstants        |     The number of constant   fields per class. |
| numOps              |     The number of methods   per  class. |
| avrgMethodVisibility|     The average value of   method visibility per class. |
| setters             |     The number of set methods   per class. |
| getters             |     The number of   get-methods per class. |
| staticness          |     The number of static methods   per class. |
| nesting             |     The nesting level per   class. |
| abstractness        |     The ratio of abstract   classes to all classes in a package. |
| numCls              |     The number of classes per   package. |
| numInterf           |     The number of   interfaces in a package. |
| packageNesting      |     The nesting level per   package. |
| numOpsCls           |     The number of class operations   per package. |
| iFImpl              |     The number of   implemented interfaces by a class. |
| NOC                 |     The number of children   per class. |
| numDesc             |     The number of descendants   per class. |
| numAnc              |     The number of ancestors   per class. |
| iC_Attr             |     The number of classes   or interfaces used as attributes in a class. |
| eC_Attr             |     The number of external uses   of a class as an attribute in other classes. |
| iC_Par              |     The number of classes   or interfaces used as parameter types in class methods. |
| eC_Par              |     The number of external uses   of a class as parameter type in methods. |
| Dep_In              |     The number of elements   that depend on a class. |
| Dep_Out             |     The number of elements that   are depended on by a class. |
| NumAssEl_ssc        |     The number of associated   elements in the same namespace of a class. |
| NumAssEl_nsb        |     The number of   associated elements that are not in the same namespace of a class. |

These metrics are also used to create configurations that will be used as fitness functions in their algorithms (before the performed modifications, A-CMA has only a limited number of search-based algorithms). We have modified the A-CMA tool in order to add DQN-based agents to it. The major modifications were (1) adding 10 new metrics used for the segregation criteria and (2) adding the proposed algorithms such as the learner, accountant, and gambler accountant agents, and finally (3) introducing a mechanism to segregate the actions. For calculating the reward in each step, we have used the built-in score function.

We also needed a Deep Q-Network (DQN in short). Therefore, we have created another project coded in the Python language named [A-CMA DQN server](https://github.com/hrahmadi71/acma_dqn_server). DQN is a deep neural network that is supposed to take the state of the problem as input and give the Q-values of each action as the output.

### Architecture
The architecture of the refactoring tool is presented in figure below.

![ACMA Architecture](/media/arch.png)

All of these units are collaborating to refactor the software.
