import time
from sys import stdout


def progress_bar(current: int, total: int, prefix: str='', suffix: str='', bar_size=40):
    full_len = int(current * bar_size / total + 0.5)
    empty_len = bar_size - full_len
    if prefix:
        prefix = prefix + ' '
    print('\r\x1b[K', end='')
    print('{}[{}{}] ({}/{}) {}'.format(prefix, '#' * full_len, '.' * empty_len, current, total, suffix), end='')
    if current == total:
        print()
    stdout.flush()


tot = 200
for i in range(tot + 1):
    progress_bar(i, tot, 'Progress bar', 'Test')
    time.sleep(0.05)
