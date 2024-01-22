package main

import (
	"log"

	"goftp.io/server/v2"
	"goftp.io/server/v2/driver/file"
)

var ServerId string = "3"

func main() {
	driver, err := file.NewDriver("./archivos")
	if err != nil {
		log.Fatal(err)
	}

	s, err := server.NewServer(&server.Options{
		Driver: driver,
		Auth: &server.SimpleAuth{
			Name:     "admin",
			Password: "admin",
		},
		Perm:      server.NewSimplePerm("root", "root"),
		RateLimit: 1000000, // 1MB/s limit
		Port:      2023,
	})
	if err != nil {
		log.Fatal(err)
	}

	if err := s.ListenAndServe(); err != nil {
		log.Fatal(err)
	}
}
