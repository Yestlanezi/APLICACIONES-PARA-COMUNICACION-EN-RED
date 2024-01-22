package game_client

import (
	"fmt"
	"net"
	"os"

	"udp_go/factory"
	"udp_go/udp"
	"udp_go/utils"
)

func Client() {
	var data factory.GameData
	udpAddress, err := net.ResolveUDPAddr("udp", ":1053")
	if err != nil {
		println("ResolveUDPAddr failed:", err.Error())
	}
	data, err = StartGame(udpAddress)
	if err != nil {
		fmt.Println("Error starting game: ", err)
		return
	}
	utils.PrintGrid(data.M)
	for data.End_game == false {
		if data.Win_game == true {
			fmt.Println("YOU WIN")
			return
		}
		fmt.Print("Go [x y [f-flag|v-unflag|u-unfold]]: ")
		// if _, err := fmt.Fscan(os.Stdin, &j, &i, &cmd); err != nil {
		if _, err := fmt.Fscan(os.Stdin, &data.Cols, &data.Rows, &data.Cmd); err != nil {
			fmt.Println("failed to read an action:", err)
			continue
		}
		data, err = udp.SendServer(udpAddress, data)
		if err != nil {
			fmt.Println("Error continuing game: ", err)
			return
		}
		utils.PrintGrid(data.M)
		fmt.Println(data.Msg)
	}
	fmt.Println("GAME OVER")
}

func StartGame(udpAddress *net.UDPAddr) (response factory.GameData, err error) {
	var data factory.GameData
	fmt.Print("Enter [rows cols]: ")
	if _, err = fmt.Fscan(os.Stdin, &data.Rows, &data.Cols); err != nil {
		fmt.Println("failed to read rows & cols:", err)
		return
	}
	data.GameId = -1
	response, err = udp.SendServer(udpAddress, data)
	return
}
