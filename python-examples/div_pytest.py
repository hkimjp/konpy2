import pytest


def div(x, y):
    """
    doctest は hkimura が足しました。
    >>> div(3, 1)
    3
    """
    return x // y


def test_div_normal():
    assert div(6, 3) == 2
    assert div(7, 2) == 3
    assert div(-8, 2) == -4
    assert div(8, -2) == -4
    assert div(-9, -3) == 3


def test_div_zero_dividend():
    assert div(0, 5) == 0


def test_div_division_by_zero():
    with pytest.raises(ZeroDivisionError):
        div(5, 0)
