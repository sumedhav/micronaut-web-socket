//Establish the WebSocket connection and set up event handlers
var hash = document.location.hash.split("/");

if (hash.length !== 3) {
    alert("Specify URI with a topic and username. Example http://localhost:8080#/stuff/bob")
}

var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/" + hash[1] + "/" + hash[2]);
webSocket.onmessage = function (msg) {
    console.log(msg);
    updateDiv(msg);
};
webSocket.onclose = function () { alert("WebSocket connection closed") };

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
    }
}

//Update the chat-panel, and the list of connected users
function updateDiv(msg) {
    var username = msg.data.split("] ")[0]
    var message = msg.data.split("] ")[1]
    var coordinates = message.split(": ")[1].split(",")
    if(username === "[" + hash[2]) {
        console.log(msg.data);
    }
    else {
        $( "#draggable" ).position({
            my: "left+" +coordinates[0]+ " top+" + coordinates[1],
            at: "left top",
            of: "body"
        });
    }
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

var $start_counter = $( "#event-start" ),
    $drag_counter = $( "#event-drag" ),
    $stop_counter = $( "#event-stop" ),
    counts = [ 0, 0, 0 ];

$( "#draggable" ).draggable({
    start: function() {
        counts[ 0 ]++;
        updateCounterStatus( $start_counter, counts[ 0 ] );
    },
    drag: function() {
        counts[ 1 ]++;
        updateCounterStatus( $drag_counter, counts[ 1 ] );
    },
    stop: function() {
        counts[ 2 ]++;
        updateCounterStatus( $stop_counter, counts[ 2 ] );
        var element = document.getElementById("draggable");
        var position = element.getBoundingClientRect();
        // console.log("x=" + position.left);
        // console.log("y=" + position.top);
        var x = Math.round((position.left + Number.EPSILON) * 100) / 100;
        var y = Math.round((position.top + Number.EPSILON) * 100) / 100;
        sendMessage("New coordinates: " + x + "," + y);
    }
});

function updateCounterStatus( $event_counter, new_count ) {
    // first update the status visually...
    if ( !$event_counter.hasClass( "ui-state-hover" ) ) {
        $event_counter.addClass( "ui-state-hover" )
            .siblings().removeClass( "ui-state-hover" );
    }
    // ...then update the numbers
    $( "span.count", $event_counter ).text( new_count );
}


