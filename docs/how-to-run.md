---
layout: default
title: How to run
nav_order: 2
---

# How to run
{: .no_toc }

To run A-CMA DQN-based agents, you should run two applications along side: the [A-CMA refactoring tool](//github.com/hrahmadi71/a-cma) and [its DQN server](//github.com/hrahmadi71/acma_dqn_server).

### Run DQN server
DQN server contains a deep neural network named Deep Q-Network which is developed by python language. So in order to run DQN server, you should have [python 3](https://python.org) installed and running on your computer.

First of all, clone DQN server from GitHub:
```terminal
$ git clone https://github.com/hrahmadi71/acma_dqn_server.git
```
Then you should go to the directory and create a virtual environment in it:
```terminal
$ cd acma_dqn_server
$ python3 -m venv .venv
```
After you've create virtual environment, you should enable it. On Linux or Mac, use this command:
```terminal
$ source .venv/bin/activate
```
On Windows, use this command:
```terminal
$ .venv\Scrips\activate
```
After enabling virtual environment, you can install the requirements of DQN server:
```terminal
(.venv)$ pip install -r requirements.txt
```
Then run the server (on the default address which is `localhost:5000`):
```terminal
(.venv)$ flask run
```
There's 4 pre-trained models which you can use for refactoring: `model_1` to `model_4`.
To use one of pretrained models, go to address `localhost:5000` via a web-browser, you must see a swagger panel. Then in the swagger panel, click on `/load_weights/` then click on `Try it out` button and change the `string` to the name of a model. for example:
```json
{
  "model_name": "model_1"
}
```

