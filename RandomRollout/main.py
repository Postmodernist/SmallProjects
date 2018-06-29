import random

import numpy as np

from game import Game, State, BOARD_SIDE, INDEX_BOARD


def get_neighbors():
    """ Get neighbors for each position """
    # Create framed board
    fb_side = BOARD_SIDE + 2
    framed_board = np.full(fb_side ** 2, -1)
    framed_board.reshape(fb_side, fb_side)[1:-1, 1:-1] = INDEX_BOARD.reshape(BOARD_SIDE, BOARD_SIDE)
    # Get neighbors
    neighbors = {}
    for i in np.arange(fb_side ** 2).reshape(fb_side, fb_side)[1:-1, 1:-1].ravel():
        ni = [i - fb_side - 1, i - fb_side, i - fb_side + 1, i - 1, i + 1, i + fb_side - 1, i + fb_side,
              i + fb_side + 1]
        neighbors[framed_board[i]] = list(filter(lambda x: x != -1, framed_board[ni]))
    return neighbors


NEIGHBORS = get_neighbors()


def _rollout(state: State):
    """ Return a result of a random rollout """
    board = state.board.copy()
    player = state.player
    valid_actions = set(state.allowed_actions)
    print(state)
    print(valid_actions)
    # Random descent until game end
    while not State.is_player_won(board, -player):
        # Make random move
        action = random.choice(list(valid_actions))
        print(action)
        board[action] = player
        state.board = board
        print(state)
        player = -player
        # Update valid actions
        valid_actions.remove(action)
        empty_neighbors = [x for x in NEIGHBORS[action] if board[x] == 0]
        valid_actions.update(empty_neighbors)
        print(valid_actions)
        if len(valid_actions) == 0:
            # Game draw
            return 0
    print()
    return -1 if player == state.player else 1


env = Game()
_rollout(env.state)
