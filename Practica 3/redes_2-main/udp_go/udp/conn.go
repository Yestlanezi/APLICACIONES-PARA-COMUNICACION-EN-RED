package udp

import (
	"bytes"
	"fmt"
	"net"
	"udp_go/factory"
	"udp_go/utils"
)

func SendUdp(udpAddress *net.UDPAddr, input []byte) (response []byte, err error) {
	conn, err := net.DialUDP("udp", nil, udpAddress)
	if err != nil {
		println("Listen failed:", err.Error())
		return
	}
	defer conn.Close()

	_, err = conn.Write(input)
	if err != nil {
		println("Write data failed:", err.Error())
		return
	}
	response = make([]byte, 1024)
	_, err = conn.Read(response)
	if err != nil {
		println("Read data failed:", err.Error())
		return
	}
	return
}

func SendServer(udpAddress *net.UDPAddr, data factory.GameData) (response factory.GameData, err error) {
	var struct_buffer bytes.Buffer
	err = utils.Encode(data, &struct_buffer)
	if err != nil {
		fmt.Println("encode error:", err)
		return
	}
	buff_response, err := SendUdp(udpAddress, struct_buffer.Bytes())
	if err != nil {
		fmt.Println("Error: ", err)
		return
	}
	err = utils.Decode(&response, buff_response)
	if err != nil {
		fmt.Println("decode error:", err)
		return
	}
	return
}
