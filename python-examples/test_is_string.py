def test_is_string(x):
    assert is_string("x") is True
    assert is_string(3) is False
    assert is_string([3.14]) is False
    
