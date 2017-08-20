$("document").ready(function() {

	//Check the game status if already started move the user to appropriate page
        $.ajax({
                method: "GET",
                url: "/gameStatus",
                cache: false,
            })
            .done(function(msg) {
                //console.log(msg);
                $("#parentDiv").hide();
                if (msg["gameStatus"] == "started") {
                    $("#joinGame").show();
                } else if (msg["gameStatus"] == "player1Ready") {
                    $("#joinGame").click();
                } else if (msg["gameStatus"] == "player2Ready") {
                    $("#joinGame").click();
                } else if (msg["gameStatus"] == "GameOver") {
                    stopMessage();
                    $("#reStartGame").click();
                } else {
                    $("#startGame").show();

                }
            });
        
        //Open about game dialog box 
        $("#helpDialogDiv").on("click", function(){
            $("#helpDialog").dialog();
            return false;
        });
        
        //Open contact us dialog box
        $("#contactusDialogMenu").on("click", function(){
            $("#contactusDialog").dialog();
            return false;
        });
        
        //start the game api call
        $("#startGame").on("click", function() {
        	$("#parentDiv").show();
        	$("#startGameDiv").hide();
	        $.ajax({
	                method: "GET",
	                url: "/startGame",
	                cache: false,
	            })
	            .done(function(msg) {
	                //console.log("gameStarted" + msg);
	                if ("falilure" == msg) {
	                    location.reload();
	                }
	                $("#p1baLabel").show()
	                player = "playerone";
	                connect();
	            });
        });

        //Join the game api call and helps user land on to the appropriate page
        $("#joinGame").on("click", function() {
	        player = "playertwo";
	        connect();
	        setTimeout(function() {
	            $("#p2baLabel").show()
	            $.ajax({
	                    method: "GET",
	                    url: "/gameStatus",
	                    cache: false,
	                })
	                .done(function(msg) {
	                    //console.log("gameStarted" + msg);
	                    if (msg["gameStatus"] == "player1Ready") {
	                        $("#startGameDiv").hide();
	                        $("#waitText").hide();
	                        $('#player2').show();
	                        $("#parentDiv").show();
	                        $("#battleshipcols").attr("disabled", "disabled");
	                        document.getElementById('battleshipcols').value = msg["battleAreaCols"];
	                        document.getElementById('battleshiprows').value = msg["battleAreaRow"];
	                        document.getElementById('playercount').value = msg["playerCount"];
	                        $('#shipcount').val(msg["shipCount"]).change();
	                    } else if (msg["gameStatus"] == "player2Ready") {
	                        document.getElementById('battleshipcols').value = msg["battleAreaCols"];
	                        document.getElementById('battleshiprows').value = msg["battleAreaRow"];
	                        document.getElementById('playercount').value = msg["playerCount"];
	                        $('#shipcount').val(msg["shipCount"]).change();
	                        selectedValues = msg["selectedValues"];
	                        $.each(selectedValues, function(key, value) {
	                            var abc = document.getElementById(value.key).closest('td')
	                            abc.setAttribute('class', 'draggable');
	                        });
	                        $("#waitText").hide();
	                        $('#player2').click();
	                    } else {
	                        $("#waitText").show();
	                        $("#joinGame").hide();
	                    }
	                });
	        }, 500);
        });
        // On click event for starting game by player 1
        $('#player1').on("click", function() {
	        // check if all valid entries present
	        player = "playerone";
	        if (document.getElementById('battleshipcols').validity.patternMismatch ||
	            document.getElementById('battleshiprows').validity.patternMismatch ||
	            document.getElementById('shipcount').validity.patternMismatch ||
	            document.getElementById('playercount').validity.patternMismatch) {
	            return false;
	        }
	        if ($("select:enabled").val()) {
	            $("#dialog").dialog();
	            return false;
	        }
	        addTable("playerTwoBattleArea");
	        $("#playerTwoBattleAreadiv").css({
	            "float": "right"
	        });
	
	        $("#p1ba").show();
	        $("#p2ba").show();
	        $("#parentDiv").hide();
	        $("#playerOneBattleArea").attr("class", "battleAreaViewOnly");
	        $("#playerOneBattleArea").off();
	        $("#playerTwoBattleArea").attr("class", "battleAreaViewOnly");
	        $("#playerTwoBattleArea").off();
	        $("#waitTextp2").show();
	        startGame("player1Ready");
	    });

    // On click event for starting game by player 1
    $('#player2').on("click", function() {
        player = "playertwo";
        if ($("select:enabled").val()) {
            $("#dialog").dialog();
            return false;
        }
        // check if all valid entries present
        addTable("playerOneBattleArea");
        $("#playerTwoBattleAreadiv").css({
            "float": "right"
        });
        $("#p1ba").show();
        $("#p2ba").show();
        $("#waitTextTurn").show();
        $("#parentDiv").hide();
        $("#playerTwoBattleArea").attr("class", "battleAreaViewOnly");
        $("#playerTwoBattleArea").off();
        $("#playerOneBattleArea").attr("class", "battleAreaViewOnly");
        $("#playerOneBattleArea").off();
        startGame("player2Ready");
    });

    // send start game message to server
    function startGame(playerStatus) {
        message["player"] = player;
        message["messageType"] = playerStatus;
        message["tableValues"] = tableValues;
        message["battleAreaRow"] = document.getElementById('battleshiprows').value;
        message["battleAreaCols"] = document.getElementById('battleshipcols').value;
        message["shipCount"] = document.getElementById('shipcount').value;
        message["playerCount"] = document.getElementById('playercount').value;
        message["selectedValues"] = selectedValues;
        //console.log(JSON.stringify(message));
        sendMessage(JSON.stringify(message));
    }
    
    //validate battleship rows area value
    $('#battleshiprows').on("change", function() {
        if (document.getElementById('battleshiprows').validity.patternMismatch ||
            document.getElementById('battleshipcols').validity.patternMismatch) {
            $("#dialog1").dialog();
            return false;
        } else {
            $('#playercount').attr("disabled", false)

        }
    });

    // On change event to display start game by participants
    $('#playercount').on("change", function() {
        if (document.getElementById('battleshiprows').validity.patternMismatch ||
            document.getElementById('battleshipcols').validity.patternMismatch ||
            document.getElementById('playercount').validity.patternMismatch) {
            $("#dialog1").dialog();
            return false;
        } else {
            $('#shipcount').attr("disabled", false);
        }
    });

    //validate battleship cols area value
    $('#battleshipcols').on("change", function() {
        if (document.getElementById('battleshipcols').validity.patternMismatch) {
            $("#dialog1").dialog();
            return false;
        } else {
            $('#battleshiprows').attr("disabled", false)
        }
    });


    // On click event to identify hit success and hit failure
    $(".battleAreaViewOnly").on("click", "table tr td", function() {
        return false;
    });

    //validate shipcount rows area value
    $('#shipcount').change(function() {

        if (document.getElementById('battleshipcols').validity.patternMismatch ||
            document.getElementById('battleshiprows').validity.patternMismatch ||
            document.getElementById('shipcount').validity.patternMismatch ||
            document.getElementById('playercount').validity.patternMismatch){
            $("#dialog1").dialog();
            return false;
        }
        $("#shiparealabel").show();
        var selectdiv = document.getElementById("shipLocation");

        // clearing earlier values
        selectedValues = [];
        selectdiv.innerHTML = "";
        if (player == "playerone") {
            $('#player1').show();
            addTable("playerOneBattleArea");
        } else {
            $('#player2').show();
            addTable("playerTwoBattleArea");
        }

        for (var x = 0; x < $(this).val(); x++) {
            var select = document.createElement("select");
            var label = document.createElement("Label");
            label.setAttribute("class", "labelclass");
            var br = document.createElement("br");

            if (x < 4) {
                label.innerHTML = "Ship Size (1*" + (1 + x) + ") ";
                select.setAttribute('data-length', 1 + x);
            } else {
                label.innerHTML = "Ship Size (1*" + 1 + ") ";
                select.setAttribute('data-length', 1);
            }
            selectdiv.appendChild(label);
            select.setAttribute('data-loc', 'H');
            select.setAttribute('class', 'selectShipLocation');
            select.setAttribute('id', 'selectShipLocation' + 1 + x);
            var option = document.createElement("option");
            option.value = "default";
            option.innerHTML = "Please Choose...";

            select.appendChild(option);
            for (var k in tableValues) {
                if (tableValues.hasOwnProperty(k)) {
                    var option = document.createElement("option");
                    option.value = k;
                    option.innerHTML = tableValues[k];
                    select.appendChild(option);
                }
            }
            selectdiv.appendChild(select);
            selectdiv.appendChild(br);
            select.addEventListener('change', function() {
                //console.log("data-length" + $(this).attr('data-length'));

                for (var k = 0; k < $(this).attr('data-length'); k++) {

                    var flag = document.getElementById(parseInt($(this).val()) + k);
                    // console.log("flag"+flag.value);
                    var returnFlag = true;
                    if (flag) {

                        var xxx = parseInt($(this).val()) + k;
                        $.each(selectedValues, function(key, value) {

                            if (xxx == value.key) {
                                $("#dialog2").dialog();
                                returnFlag = false;
                            }
                        });
                    } else {
                        $("#dialog2").dialog();
                        returnFlag = false;
                    }
                    if (!returnFlag) {
                        return false;
                    }
                }
                for (var k = 0; k < $(this).attr('data-length'); k++) {
                    var index1 = parseInt($(this).val()) + k;
                    selectedValues.push({
                        "key": index1,
                        "hit": false
                    });

                    var abc = document.getElementById(index1).closest('td')
                    abc.setAttribute('class', 'draggable');

                }

                $.each(selectedValues, function(key, value) {
                    $('select').find("option[value=" + value.key + "]").remove();
                    //console.log(value.key + " : " + value.hit);
                });

                //console.log(selectedValues);
                 $(this).attr('disabled', 'disabled');
            });
        }
    });

	//Add click event on restart button
	$("#reStartGame").on("click", function() {
	    //console.log("restart game");
	    $.ajax({
	            method: "GET",
	            url: "/reStartGame",
	            cache: false,
	        })
	        .done(function(msg) {
	            //console.log("gameStarted" + msg);
	            location.reload();
	        });
	});
});
var player;
var message = {};
var tableValues = {};
var selectedValues = [];

// On click event to identify hit success and hit failure
function TableClick(divId) {
    $(divId).on("click", "table tr td", function() {
        var tdId = $(this).attr('id');
        if (player == "playerone") {
            message["messageType"] = "player1Hit";
            message["player"] = "playerone";
        } else {
            message["messageType"] = "player2Hit";
            message["player"] = "playertwo";
        }
        message["hitKey"] = tdId;
        sendMessage(JSON.stringify(message));
    });
}

// Function to create table having divId as provided while calling this method
function addTable(divId) {
    var myTableDiv = document.getElementById(divId);
    myTableDiv.innerHTML = "";
    var table = document.createElement('TABLE');
    table.setAttribute('id', 'selectedTable')
    table.border = '1';

    var tableBody = document.createElement('TBODY');
    table.appendChild(tableBody);

    var tr = document.createElement('TR');
    var th = document.createElement('TH');
    th.setAttribute('class', "ui-widget-header");

    tr.appendChild(th);

    for (var j = 0; j < document.getElementById("battleshipcols").value; j++) {
        var th = document.createElement('TH');
        th.width = '47';
        th.appendChild(document.createTextNode(j + 1));
        th.setAttribute('class', "ui-widget-header");
        th.boder = '1';
        th.background = 'grey';
        tr.appendChild(th);
        tableBody.appendChild(tr);
    }

    for (var i = 0; i < document.getElementById("battleshiprows").value.toLowerCase().charCodeAt(0) - 97 + 1; i++) {
        var tr = document.createElement('TR');
        tableBody.appendChild(tr);
        var th = document.createElement('TH');
        th.setAttribute('class', "ui-widget-header");
        th.width = '47';
        th.appendChild(document.createTextNode(String.fromCharCode('A'.charCodeAt() + i)));
        tr.appendChild(th);


        for (var j = 0; j < document.getElementById("battleshipcols").value; j++) {
            var td = document.createElement('TD');
            td.width = '47';
            tableValues[(i + 1) + "" + j] = String.fromCharCode('A'.charCodeAt() + i) + (j + 1);
            td.appendChild(document.createTextNode(String.fromCharCode('A'.charCodeAt() + i) + (j + 1)));
            td.setAttribute('class', '.ui-widget-content')
            tr.appendChild(td);
            td.setAttribute('id', (i + 1) + "" + j)
        }
    }
    myTableDiv.appendChild(table);
    //console.log("table values " + tableValues);
}


//******************************************Websocket configuration - Start ************************************
var stompClient = null;

//Web socket configuration to connect and subscribe
function connect() {
    var socket = new SockJS("/messages");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        //console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function(message) {
            //console.log('Message received from server: ' + message);
            var messageBody = JSON.parse(message.body);
            processMessage(messageBody);
        });
    });
}

//Web socket configuration to disconnect
function stopMessage() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    //console.log("Disconnected");
}

//Web Socket send message
function sendMessage(message) {
    stompClient.send("/app/messages", {}, message);
}
//******************************************Web socket configuration - End************************************
//******************************************Web socket Message Processing - End************************************

//Web Socket response processing having message type as player1Ready
function player1Ready(message) {
    if (player == "playertwo") {
        location.reload();
    }
}

//Web Socket response processing having message type as player2Ready
function player2Ready(message) {
    if (player == "playerone") {
        $("#playerTwoBattleArea").attr("class", "battleArea");
        $("#playerTwoBattleArea").on();
        $("#yourTurn").show();
        TableClick("#playerTwoBattleArea");
    }
    $("#waitTextp2").hide();
}

//Web Socket response processing having failure response
function displayError() {
	location.reload();
}

//Web Socket response processing having message type as GameOver
function gameOver(message) {
    hitResponseSuccess(message);
    if (player == "playerone" && message["player"] == "playerone") {
        $("#winner").show();
    }
    if (player == "playerone" && message["player"] == "playertwo") {
        $("#loser").show();
    }
    if (player == "playertwo" && message["player"] == "playertwo") {
        $("#winner").show();
    }
    if (player == "playertwo" && message["player"] == "playerone") {
        $("#loser").show();
    }
    $("#playerTwoBattleArea").off();
    $("#yourTurn").hide();
    $("#waitTextTurn").hide();
    $("#playerOneBattleArea").off();
    $("#parentDiv").hide();
    stopMessage();
}

//Web Socket response processing having message type as HitSuccess
function hitResponseSuccess(message) {
    if (player == "playerone" && message["player"] == "playerone") {
    	//console.log("player1 acting for player 1");
        $("#playerTwoBattleArea").find("td#" + message["hitKey"]).attr("class", "hitsuccess");
    }
    if (player == "playerone" && message["player"] == "playertwo") {
        //console.log("player1 acting for player 2");
        $("#playerOneBattleArea").find("td#" + message["hitKey"]).attr("class", "hitsuccessorange");
    }
    if (player == "playertwo" && message["player"] == "playertwo") {
        //console.log("player2 acting for player 2");
        $("#playerOneBattleArea").find("td#" + message["hitKey"]).attr("class", "hitsuccess");
    }
    if (player == "playertwo" && message["player"] == "playerone") {
        //console.log("player2 acting for player 1");
        $("#playerTwoBattleArea").find("td#" + message["hitKey"]).attr("class", "hitsuccessorange");
    }
}

//Web Socket response processing having message type as HitFailure
function hitResponseFailure(message) {
    if (player == "playerone" && message["player"] == "playerone") {
        $("#waitTextTurn").show();
        $("#playerTwoBattleArea").attr("class", "battleAreaViewOnly");
        $("#playerTwoBattleArea").off();
        $("#yourTurn").hide();
        $("#playerTwoBattleArea").find("td#" + message["hitKey"]).attr("class", "hitfailure");
       // console.log("player1 acting for player 1");
    }
    if (player == "playerone" && message["player"] == "playertwo") {
        $("#waitTextTurn").hide();
        $("#playerTwoBattleArea").attr("class", "battleArea");
        $("#playerTwoBattleArea").on();
        TableClick("#playerTwoBattleArea");
        $("#yourTurn").show();
        $("#playerOneBattleArea").find("td#" + message["hitKey"]).attr("class", "hitfailure");
        //console.log("player1 acting for player 2");
    }
    if (player == "playertwo" && message["player"] == "playertwo") {
        $("#waitTextTurn").show();
        $("#playerOneBattleArea").attr("class", "battleAreaViewOnly");
        $("#playerOneBattleArea").off();
        $("#yourTurn").hide();
        $("#playerOneBattleArea").find("td#" + message["hitKey"]).attr("class", "hitfailure");
        //console.log("player2 acting for player 2");
    }
    if (player == "playertwo" && message["player"] == "playerone") {
        $("#waitTextTurn").hide();
        $("#playerOneBattleArea").attr("class", "battleArea");
        $("#playerOneBattleArea").on();
        TableClick("#playerOneBattleArea");
        $("#yourTurn").show();
        $("#playerTwoBattleArea").find("td#" + message["hitKey"]).attr("class", "hitfailure");
        //console.log("player2 acting for player 1");
    }
}

//Web Socket response processing for all types of response messages
function processMessage(messagebody) {
    switch (messagebody["messageType"]) {
        case "player2Ready":
            player2Ready(messagebody);
            break;
        case "player1Ready":
            player1Ready(messagebody);
            break;
        case "hitSuccess":
            hitResponseSuccess(messagebody);
            break;
        case "hitFailure":
            hitResponseFailure(messagebody);
            break;
        case "GameOver":
            gameOver(messagebody);
            break;
        default :
        	displayError();
    }
}