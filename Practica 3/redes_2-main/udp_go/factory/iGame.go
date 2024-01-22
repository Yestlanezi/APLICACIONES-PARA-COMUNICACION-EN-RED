package factory

import "github.com/regeda/minesweeper"

type IGame interface {
	SetGrid()
	StartGame()
	SetId(gameId int)
	GetId() int
	GetGame() *Game
	GetGameData() GameData
	SetEndGame()
	GetEndGame() bool
	Flag()
	Unflag()
	Unfold()
	GetCmd() string
	SetGame(game *minesweeper.Game)
	SetData(data GameData)
}
