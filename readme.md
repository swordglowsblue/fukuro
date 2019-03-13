# Fukuro
<img src="logo.svg" alt="Fukuro logo" width="256" height="256">

Fukuro (from the Japanese ふくろう *fukurou*, meaning "owl") is a programming language designed for simplicity, minimalism, 
    and ease of use. It is statically-typed, type-inferred, null-free, immutable by default, and tail-call optimized.
    
**Fukuro is still in heavy development and is not currently functional.**
    
## Rough syntax plans
```
let var1 = 100   # Define an immutable variable
mut var2 = 100   # Define a mutable variable
var2 = 200       # Redefine a mutable variable

# Functions
fun fibonacci(n: Int): Int {
    if(n <= 1) return 1
    fibonacci((n-1) + (n-2))
}

fibonacci(7)   # => 13

# Lambdas
let add1 = { n | n + 1 }
add1(5)   # => 6

# Higher order functions, lambda argument shorthand
let add2 = { it() + 2 }
add2 { 5 }   # => 7

# Structs
struct Point {
    x: Int
    y: Int
}

let pnt = Point(3, 4)
pnt.x   # => 3
pnt.y   # => 4

# Extension functions, single-expr function shorthand
fun Point.offset(x: Int, y: Int) = Point(this.x + x, this.y + y)
pnt.offset(1, -2)   # => Point(4, 2)

# Operator overloading
fun Point.+(other: Point) = this.offset(other.x, other.y)
Point(1, 2) + Point(2, 3)   # => Point(3, 5)
```
