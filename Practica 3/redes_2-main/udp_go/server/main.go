package main

import (
	"fmt"
	"net"
	"time"
	s "udp_go/server/game_server"
)

func main() {
	s.Server()

}

func response(udpServer net.PacketConn, addr net.Addr, buf []byte) {
	fmt.Println("Buf: ", string(buf))
	time := time.Now().Format(time.ANSIC)
	responseStr := fmt.Sprintf("time received: %v. Your message: %v!", time, string(buf))

	udpServer.WriteTo([]byte(responseStr), addr)
}
