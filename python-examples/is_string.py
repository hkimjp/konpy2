def is_string(x):
    """
    引数 x が文字列であれば True を返す。

    >>> is_string("hello")
    True
    >>> is_string(123)
    False
    >>> is_string([1, 2, 3])
    False
    >>> is_string("")
    True
    """
    return isinstance(x, str)
