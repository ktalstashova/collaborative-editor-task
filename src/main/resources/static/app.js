jQuery(function ($) {
    'use strict';

    const BACKSPACE_KEY = 8;
    const ENTER_KEY = 13;
    const DELETE_KEY = 46;

    class Identifier {
        constructor(position, clientId) {
            this.position = position;
            this.clientId = clientId;
        }
    }

    class Operation {
        static TYPES = {
            INSERT: "INSERT",
            DELETE: "DELETE",
            CHANGEID: "CHANGEID"
        };

        constructor(type, pos, clientId, c) {
            this.type = type;
            this.position = pos;
            this.character = c;
            this.clientId = clientId;
        }
    }


    const Model = {
        FIRST_FAKE_KEY: 0,
        LAST_FAKE_KEY: 2147483647,

        isDebugMode: false,
        clientId: new String(Math.floor(Math.random() * 10000000000000001) + new Date().getMilliseconds()),
        // Don't need to lock on state changes as JavaScript has a concurrency model based on an "event loop".
        document: undefined,

        init: function () {
            let that = this;
            if (that.isDebugMode)
                console.debug("Init model");
            $.ajax({
                url: "http://localhost:8080/document?no-cache="+Math.random()
            }).then(function (data) {
                if (that.isDebugMode)
                    console.debug("Model has been fetched " + JSON.stringify(data));
                that.document = new SortedMap({}, that.equals,
                    that.keyComparator);
                for (let key in data) {
                    if (data.hasOwnProperty(key)) {
                        that.document.set(JSON.parse(key), data[key]);
                    }
                }
                that.print();
            });
            return this;
        },

        print: function () {
            let iterator = this.document.entries();
            let buffer = "";

            let current = iterator.next();
            while (!current.done) {
                if (!this.isBorderElement(current.value[0]))
                    buffer += current.value[1];
                current = iterator.next();
            }
            $("#editor").val(buffer);
            this.showDebugInfo();
        },
        isBorderElement: function (element) {
            return element.position.length == 1 && (element.position[0] == this.FIRST_FAKE_KEY || element.position[0] == this.LAST_FAKE_KEY);
        },
        keyComparator: function (aId, bId) { // Comparator
            if (aId === bId) return 0;

            let a = aId.position;
            let b = bId.position;
            let i = 0;
            let minLength = Math.min(a.length, b.length);
            while (i < minLength) {
                if (a[i] == b[i]) {
                    i++;
                } else
                    return (a[i] > b[i]) ? 1 : -1;
            }
            if (a.length == b.length)
                return 0;
            else
                return (a.length > b.length) ? 1 : -1;
        },
        equals: function (a, b) {
            if (a.position.length != b.position.length)
                return false;
            for (let i = 0; i < a.position.length; i++) {
                if (a.position[i] != b.position[i]) {
                    return false;
                }
            }
            return true;
        },

        showDebugInfo: function () {
            //if (!this.isDebugMode) return;
            let iterator = this.document.entries();
            let buffer = "State:\n";

            let current = iterator.next();
            while (!current.done) {
                buffer += current.value[0].position + " " + current.value[0].clientId + ":" + current.value[1] + "\n";
                current = iterator.next();
            }
            $("#modelState").val(buffer);
        },

        getElementByCursorPosition(position) {
            let iterator = this.document.keys();
            let current = iterator.next();
            while (!current.done) {
                if (position-- <= 0) {
                    return current.value;
                }
                current = iterator.next();
            }
            return undefined;
        },

        getSurroundingElements: function (cursorPosition) {
            let position = (cursorPosition < this.document.size - 1) ? cursorPosition : this.document.size - 2;
            return {
                prev: this.getElementByCursorPosition(position),
                next: this.getElementByCursorPosition(position + 1)
            };
        },

        generateNewPosition: function (prev, next) {
            //key generation based on random
            let prevPos = prev.position;
            let nextPos = next.position;
            let i = 0;
            let newPos = [];
            let minLength = Math.min(prevPos.length, nextPos.length);
            while (true) {
                if (i == minLength) {
                    // as we take "prev" and "next" elements from sorted map so the number of digits in the position is always bigger for a "next" element
                    newPos[i] = Math.floor(Math.random() * nextPos[i]);
                    return newPos;
                }
                if (prevPos[i] == nextPos[i]) {
                    newPos[i] = prevPos[i];
                    i++;
                } else {
                    let minEl, maxEl;
                    if (nextPos[i] - prevPos[i] == 1) {
                        newPos[i] = prevPos[i];
                        minEl = 0;
                        while (prevPos[i + 1] != undefined) {
                            if (prevPos[i + 1] == this.LAST_FAKE_KEY) {
                                i++;
                                newPos[i] = prevPos[i];
                            } else {
                                minEl = prevPos[i + 1] + 1;
                                break;
                            }
                        }
                        maxEl = this.LAST_FAKE_KEY;
                        i++;
                    } else {
                        minEl = prevPos[i] + 1;
                        maxEl = nextPos[i];
                    }
                    newPos[i] = Math.floor(Math.random() * (maxEl - minEl) + minEl); //add next element in [minEl,maxEl) range
                    return newPos;
                }
            }
        },

        insertAtPosition: function (cursorPosition, text) {
            let ids = this.getSurroundingElements(cursorPosition)
            if (this.isDebugMode)
                console.log('Surrounding IDs for cursorPosition: ' + cursorPosition + ' are:' + JSON.stringify(ids));
            let newElPosition = this.generateNewPosition(ids.prev, ids.next);
            if (this.isDebugMode)
                console.log('New Element position:' + newElPosition);
            this.document.set(new Identifier(newElPosition, this.clientId), text);
            this.showDebugInfo();
            WebSocket.sendOperation(new Operation(Operation.TYPES.INSERT, newElPosition, this.clientId, text));
        },

        deleteAtPosition: function (cursorPosition) {
            let currentKey = this.getElementByCursorPosition(cursorPosition + 1);
            if (this.isBorderElement(currentKey)) return; // Never remove last and first elements
            this.document.delete(currentKey);
            this.showDebugInfo();
            WebSocket.sendOperation(new Operation(Operation.TYPES.DELETE, currentKey.position, this.clientId));
        },

        update: function (operation) {
            if (operation.clientId == this.clientId && operation.type != Operation.TYPES.CHANGEID)
                return;
            if (operation.type == Operation.TYPES.INSERT || operation.type == Operation.TYPES.CHANGEID) {
                this.document.set(new Identifier(operation.position, operation.clientId), operation.character);
            } else if (operation.type == Operation.TYPES.DELETE) {
                this.document.delete(new Identifier(operation.position, operation.clientId));
            }
            this.print();
        }
    };

    const App = {
        uiModel: undefined,

        init: function (model) {
            this.uiModel = model;
            this.bindEvents();

        },
        bindEvents: function () {
            $('#editor').on('keypress', this.keypress.bind(this))
                .on('keydown', this.keydown.bind(this))
                .on('keyup', this.keyup.bind(this));
        },

        keypress: function (event) {
            if (this.assertNoSelection()) {
                let symbol = String.fromCharCode(event.charCode);
                console.log('keypress');
                this.insertAtCaret(symbol);
            }
        },

        keydown: function (e) {
            if (e.keyCode === BACKSPACE_KEY || e.keyCode === DELETE_KEY) {
                if (this.assertNoSelection()) {
                    console.log('keydown');
                    this.deleteAtCaret(e.keyCode === DELETE_KEY ? 0 : 0);
                } else {
                    e.preventDefault();
                }
            }
        },

        keyup: function (e) {
            if (e.keyCode === BACKSPACE_KEY || e.keyCode === DELETE_KEY) {
                console.log('key up');
                if (!this.assertNoSelection()) {
                    e.preventDefault();
                }
            }
        },

        getCursorPos: function () {
            return $('#editor').prop('selectionStart');
        },

        insertAtCaret: function (text) {
            let cursorPos = this.getCursorPos();
            this.uiModel.insertAtPosition(cursorPos, text);
        },

        deleteAtCaret: function (offset) {
            let cursorPos = this.getCursorPos() + offset;
            this.uiModel.deleteAtPosition(cursorPos);
        },

        assertNoSelection: function () {
            if ($('#editor').prop('selectionStart') !== $('#editor').prop('selectionEnd')) {
                alert('Operations on selection currently not supported');
                return false;
            }
            return true;
        }
    };

    const WebSocket =
        {
            isDebugMode: true,
            stompClient: null,

            connect: function () {
                let socket = new SockJS('/document-websocket');
                this.stompClient = Stomp.over(socket);

                let that = this;

                this.stompClient.connect({}, function (frame) {
                    if (that.isDebugMode)
                        console.log('Connected: ' + frame);
                    that.stompClient.subscribe('/document/operations', function (operation) {
                        if (that.isDebugMode)
                            console.log("Operation: " + operation.body);
                        Model.update(JSON.parse(operation.body));
                    });
                }, function (message) {
                    if (that.isDebugMode)
                        console.debug("Disconnected with message: " + message);
                    // Automatically reconnect
                    setTimeout(function () {
                        that.connect();
                    }, 1000);
                });
            },

            disconnect: function () {
                if (this.stompClient !== null) {
                    this.stompClient.disconnect();
                }
                if (this.isDebugMode)
                    console.debug("Disconnected");
            },
            sendOperation: function (operation) {
                if (this.isDebugMode)
                    console.debug('Send Operation:' + operation);
                this.stompClient.send("/app/operation", {}, JSON.stringify(operation));
            }
        }

    App.init(Model.init());
    WebSocket.connect();
});
