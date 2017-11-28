importPackage(java.util)
importPackage(com.randioo)

function compareObject(object1 ,object2){
	if(object1.cardNum> object2.cardNum){
		return 1 ;
	}else if(object1.cardNum< object2.cardNum) {
		return -1 ;
	}else{
		if(object1.color>object2.color){
			return 1 ;
		}else{
			return -1 ;
		}
	}
}
//
function compare(cardList1,cardlist2,cardConfigList1,cardConfigList2){
	var type1 = cardList1 ;
	var type2 = cardlist2;
	if(type1>type2){
		return 1;
	}else if(type1<type2){
		return -1 ;
	}else{
		println(cardConfigList1);
		Collections.sort(cardConfigList1,new CardComparator());
		Collections.sort(cardConfigList2,new CardComparator());
		var value1 = cardConfigList1.get(4);
		var value2 = cardConfigList2.get(4);
		var sort = new CardComparator();
		var index =sort.compare(value1,value2);
		println("index="+index);
//		var object1  = cardConfigList1 ;
//		var object2  = cardConfigList2 ;
//		object1.sort(compareObject);
//		object2.sort(compareObject);
//		return compareObject(object1[4] , object2[4]) ;
	}
	println(cardConfigList1);
}
	
function myFunction(a1,a2,a3,a4,a5,c1,c2,c3,c4,c5,d1,d2,d3,d4,d5){
	alert(0);
	 // 排序
	 var arr=new Array(a1,a2,a3,a4,a5);
	 arr.sort(); // 帮你排序 （小到大）
	 // 排序
	 var arrc=new Array(c1,c2,c3,c4,c5);
	 arrc.sort(); // 帮你排序 （小到大）
	 // 排序
	 var arrd=new Array(d1,d2,d3,d4,d5);
	 arrd.sort(); // 帮你排序 （小到大）
	// 炸弹牛
	var qiansige = (arr[0]==arr[1]&arr[1]==arr[2]&arr[2]==arr[3]) ;
	var housige = (arr[1]==arr[2]&arr[2]==arr[3]&arr[3]==arr[4]) ;
	if(qiansige||housige){
		return 1;
	}
	
	// 葫芦牛
	var qiansange = (arr[0]==arr[1]&arr[1]==arr[2]) & (arr[3]==arr[4]) ;
	var housange = (arr[2]==arr[3]&arr[3]==arr[4]) & (arr[0]==arr[1]) ;
	if(qiansange || housange){
		return 2 ;
	}
	// 同花牛
	var tongHua = (arrc[0]==arrc[1]&arrc[1]==arrc[2]&arrc[2]==arrc[3]&arrc[3]==arrc[4])  ;
	if(tongHua){
		alert("同花牛");
		return ;
	}
	alert(2);
	// 五花牛
	var wuHua= (arrc[0]>=10&arrc[1]>=10&arrc[2]>=10&arrc[3]>=10&arrc[4]>=10)  ;
	if(wuHua){
		alert("五花牛");
		return ;
	}
	alert(3);
	// 顺子牛
	var shunzi = (arrd[0]+1==arrd[1]&arrd[1]+1==arrd[2]&arrd[2]+1==arrd[3]&arrd[3]+1==arrd[4])  ;
	if(shunzi){
	    alert("顺子牛");
		return ;
	}
	// 牛九
	
	var niu= (arrd[0]+1==arrd[1]&arrd[1]+1==arrd[2]&arrd[2]+1==arrd[3]&arrd[3]+1==arrd[4])  ;
	if(niu){	
	   alert("顺子牛");
		return ;
	}

	var niu = has10(a1,a2,a3,a4,a5);
	if(niu!=-1){
		alert("you niu "+ niu)
	}else{
	   alert("wuniu");
	}


	alert("Welcome " + name + ", the " + job);
}

function has10(a, b, c, d, e) {
	var value10Array = new Array(//
	10, 11, 12, 13,// 黑桃
	23, 24, 25, 26,// 红桃
	36, 37, 38, 39, // 梅花
	49, 50, 51, 52// 方块
	);

	if (value10Array.indexOf(a)) {
		a = 10;
	}
	if (value10Array.indexOf(b)) {
		b = 10;
	}
	if (value10Array.indexOf(c)) {
		c = 10;
	}
	if (value10Array.indexOf(d)) {
		d = 10;
	}
	if (value10Array.indexOf(e)) {
		e = 10;
	}

	var arr = [ [ a, b, c ,d,e ], [ a, b, d ,c,e], [ a, b, e,c,d ], [ a, c, d,b,e ],
			[ b, c, d ,a,e], [ b, c, e,a,d ], [ b, d, e,a,c ], [ c, d, e ,a,b] ];

	for (var i = 0; i < arr.length; i++) {
		var v1 = arr[i][0];
		var v2 = arr[i][1];
		var v3 = arr[i][2];
		if (check10(v1, v2, v3)) {
			var niu = (arr[3]+arr[4])%10
			return niu;
		}
	}

	return -1;
}

function check10(a, b, c) {
	return (a + b + c) % 10 == 0;
}