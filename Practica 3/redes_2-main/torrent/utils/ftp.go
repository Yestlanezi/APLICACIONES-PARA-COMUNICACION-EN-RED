package utils

import (
	"log"

	"goftp.io/server/v2"
	"goftp.io/server/v2/driver/file"
)

func InitFTPServer(name, password, root_dir string) {
	// driver, err := file.NewDriver("./archivos")
	driver, err := file.NewDriver(root_dir)
	if err != nil {
		log.Fatal(err)
	}

	s, err := server.NewServer(&server.Options{
		Driver: driver,
		Auth: &server.SimpleAuth{
			Name:     name,
			Password: password,
		},
		Perm:      server.NewSimplePerm("root", "root"),
		RateLimit: 1000000, // 1MB/s limit
	})
	if err != nil {
		log.Fatal(err)
	}

	if err := s.ListenAndServe(); err != nil {
		log.Fatal(err)
	}
}
