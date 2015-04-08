<!-- Begin
//************ ?????
drag = 0
move = 0
function init() {
    window.document.onmouseup = WindowmouseUp
    window.document.ondragstart = WindowmouseStop
}
function WindowmouseDown(Mywindow) {
    var windowobj=eval(Mywindow);
   
    if (drag) {
        
        clickleft = window.event.x - parseInt(windowobj.style.left)
        clicktop = window.event.y - parseInt(windowobj.style.top)
        windowobj.style.zIndex += 1
        move = 1
    }
}
function WindowmouseStop() {
    window.event.returnValue = false
}
function WindowmouseMove(Mywindow) {
    var windowobj=eval(Mywindow);
    if (move) {
        windowobj.style.left = window.event.x - clickleft
        windowobj.style.top = window.event.y - clicktop
    }
}
function WindowmouseUp() {
    move = 0
}
init()

//  End -->