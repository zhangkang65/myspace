var canmove=false;
var scaleFactor=10.26;

var tempOffsetX=0;
var tempOffsetY=0;

var mapOffsetX=0;
var mapOffsetY=0;
var Starline;
var activeConcept;
var thisobj=null   //为了完成各种基本编辑功能，如“置前”“复制”“删除”等
var StartPointX;
var StartPointY;
var MaxID=1;

var Start2X=100;
var Start2Y=100;
var End2X=0;
var End2Y=0;
var curSelectObjectId;

var candrag=false;
var candraw=false;

var dash = null;
var startLeft = 0;
var startTop = 0;
function MoveOver() {}

function setMove() {}
var lineStyle="";
var lineType=1;
function MoveStart() {}

function Moving() {}

function MoveAllLine() {}

//显示并调整所有的线段
function ShowAllLine() {}

function LineDot(beginShape,endShape) {}

function getAngle(x1,y1,x2,y2){}

function UpdateOneLinePos(line) {}

function SetJoinLine(fromShape,toShape,Line){}

function GetCenterX(shape) {}

function GetCenterY(shape) {}

function EndMove() {}

function GetMapOffsetY() {}

function GetMapOffsetX() {}

function MouseUP() {}

//判断节点间是否已经有关系了
function ContainLine(fromline,Toline) {}

function selectWorkFlowItemGroup(n){}
//document.onclick = EndMove;
//Chart.onmousedown = MoveStart;
//Chart.onmouseup=MouseUP;
