# Copyright (c) 2012 Giorgos Verigakis <verigak@gmail.com>
#
# Permission to use, copy, modify, and distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

from sys import stdout

HIDE_CURSOR = '\x1b[?25l'
SHOW_CURSOR = '\x1b[?25h'


class WriteMixin(object):
    hide_cursor = False

    def __init__(self, message=None, **kwargs):
        super(WriteMixin, self).__init__(**kwargs)
        self._width = 0
        if message:
            self.message = message

        if self.hide_cursor:
            print(HIDE_CURSOR, end='')
        print(self.message, end='')
        stdout.flush()

    def write(self, s):
        b = '\b' * self._width
        c = s.ljust(self._width)
        print(b + c, end='')
        self._width = max(self._width, len(s))
        stdout.flush()

    def finish(self):
        if self.hide_cursor:
            print(SHOW_CURSOR, end='')


class WritelnMixin(object):
    hide_cursor = False

    def __init__(self, message=None, **kwargs):
        super(WritelnMixin, self).__init__(**kwargs)
        if message:
            self.message = message

        if self.hide_cursor:
            print(HIDE_CURSOR, end='')

    @staticmethod
    def clearln():
        print('\r\x1b[K', end='')

    def writeln(self, line):
        self.clearln()
        print(line, end='')
        stdout.flush()

    def finish(self):
        print()
        if self.hide_cursor:
            print(SHOW_CURSOR, end='')
