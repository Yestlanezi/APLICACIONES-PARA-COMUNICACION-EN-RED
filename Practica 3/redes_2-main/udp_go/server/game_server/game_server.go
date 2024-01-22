package game_server

import (
	"bytes"
	"fmt"
	"log"
	"math/rand"
	"net"
	"time"

	"udp_go/factory"
	"udp_go/utils"
)

var Games []factory.Game

func Server() {
	// listen to incoming udp packets
	udpServer, err := net.ListenPacket("udp", ":1053")
	if err != nil {
		log.Fatal(err)
	}
	defer udpServer.Close()

	for {
		buf := make([]byte, 1024)
		_, addr, err := udpServer.ReadFrom(buf)
		if err != nil {
			continue
		}
		go response(udpServer, addr, buf)
	}

}

func response(udpServer net.PacketConn, addr net.Addr, buf []byte) {
	var outBuffer bytes.Buffer
	var Igame factory.Game
	var data factory.GameData
	var gameId int
	err := utils.Decode(&data, buf)
	if err != nil {
		fmt.Println("decode error:", err)
	}
	gameId = data.GameId
	// fmt.Printf("data: %+v\n", data)
	if gameId == -1 {
		gameId = len(Games) + 1
		Igame.SetData(data)
		Igame.SetId(gameId)
		rand.Seed(time.Now().UnixNano())
		Igame.SetGrid()
		Igame.StartGame()
		Games = append(Games, *Igame.GetGame())
	} else {
		Games[gameId-1].Data.Cmd = data.Cmd
		Games[gameId-1].Data.Rows = data.Rows
		Games[gameId-1].Data.Cols = data.Cols
		// f_game = Games[gameId-1]
		switch Games[gameId-1].GetCmd() {
		case "f":
			Games[gameId-1].Flag()
		case "v":
			Games[gameId-1].Unflag()
		case "u":
			Games[gameId-1].Unfold()
		default:
			Games[gameId-1].Data.Msg = "Unknown command:" + Games[gameId-1].GetCmd()
		}

	}
	fmt.Printf("Game: %+v\n", Games[gameId-1])
	// game.PrintGrid(Games[gameId-1].Data.M)
	// //err = utils.Encode(responseGrid, &outBuffer)
	err = utils.Encode(Games[gameId-1].GetGameData(), &outBuffer)
	if err != nil {
		fmt.Println("encode error:", err)
	}

	udpServer.WriteTo(outBuffer.Bytes(), addr)
}
