package factory

import (
	"fmt"
	"math/rand"
	"time"

	"github.com/regeda/minesweeper"
)

type GameData struct {
	Rows     int
	Cols     int
	Cmd      string
	GameId   int
	M        minesweeper.Grid
	End_game bool
	Win_game bool
	Msg      string
}

type Game struct {
	Data GameData
	G    *minesweeper.Game
}

func (g *Game) SetGrid() {
	rand.Seed(time.Now().UnixNano())
	g.Data.M = minesweeper.GenerateGrid(g.Data.Rows, g.Data.Cols, 0.3)
}

func (g *Game) SetId(gameId int) {
	g.Data.GameId = gameId
}

func (g *Game) GetId() int {
	return g.Data.GameId
}

func (g *Game) StartGame() {
	g.G = minesweeper.New(g.Data.M)
	return
}

func (g *Game) GetGame() *Game {
	return g
}

func (g *Game) GetGameData() GameData {
	return g.Data
}

func (g *Game) SetEndGame() {
	g.Data.End_game = true
}

func (g *Game) GetEndGame() bool {
	return g.Data.End_game
}

func (g *Game) Flag() {
	g.Data.M[g.Data.Rows][g.Data.Cols].Flag(true)
}

func (g *Game) Unflag() {
	g.Data.M[g.Data.Rows][g.Data.Cols].Flag(false)
}

func (g *Game) Unfold() {
	left, ok := g.G.Unfold(g.Data.Rows, g.Data.Cols)
	if !ok {
		g.Data.End_game = true
	}
	if left == 0 {
		g.Data.Win_game = true
	}
	g.Data.Msg = "left cells: " + fmt.Sprint(left)
	return
}

func (g *Game) GetCmd() string {
	return g.Data.Cmd
}

func (g *Game) SetGame(game *minesweeper.Game) {
	g.G = game
}

func (g *Game) SetData(data GameData) {
	g.Data = data
}
