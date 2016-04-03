package me.kirimin.kotlin_osero

import com.nhaarman.mockito_kotlin.*
import me.kirimin.kotlin_osero.game.GamePresenter
import me.kirimin.kotlin_osero.game.GameView
import me.kirimin.kotlin_osero.model.Place
import me.kirimin.kotlin_osero.model.Stone
import org.junit.Test

import org.junit.Before

class GamePresenterTest {

    lateinit var viewMock: GameView
    lateinit var presenter: GamePresenter

    @Before
    fun setup() {
        presenter = GamePresenter()
        viewMock = mock()
    }

    @Test
    @JvmName(name = "開始時に中央に4つの石が置かれる")
    fun putDefaultStones() {
        presenter.onCreate(viewMock)
        verify(viewMock, times(1)).putStone(Place(3, 3, Stone.BLACK))
        verify(viewMock, times(1)).putStone(Place(4, 3, Stone.WHITE))
        verify(viewMock, times(1)).putStone(Place(3, 4, Stone.WHITE))
        verify(viewMock, times(1)).putStone(Place(4, 4, Stone.BLACK))
    }

    @Test
    @JvmName(name = "すでに石が置かれている場所には石を置けない")
    fun canPutCheck() {
        presenter.onCreate(viewMock)
        verify(viewMock, never()).putStone(Place(2, 4, Stone.WHITE))
        // 石を配置  ○● → ○○●
        presenter.putStone(Place(2, 4, Stone.WHITE))
        verify(viewMock, times(1)).putStone(Place(2, 4, Stone.WHITE))
        // 同じ位置をクリック
        presenter.onClickPlace(2, 4)
        // 石が上書きされない
        verify(viewMock, times(1)).putStone(Place(2, 4, Stone.WHITE))
        verify(viewMock, never()).putStone(Place(2, 4, Stone.BLACK))
        // 間の石も変わらない
        verify(viewMock, times(1)).putStone(Place(2, 4, Stone.WHITE))
        verify(viewMock, never()).putStone(Place(2, 4, Stone.BLACK))
    }

    @Test
    @JvmName(name = "左右に挟むと色が変わる")
    fun changeStoneAtLeftAndRight() {
        presenter.onCreate(viewMock)
        // 石を配置 x○● → ●○●
        presenter.onClickPlace(2, 4)
        // クリックした位置に置かれる
        verify(viewMock, times(1)).putStone(Place(2, 4, Stone.BLACK))
        // 挟んだ石が変わる
        verify(viewMock, times(1)).putStone(Place(3, 4, Stone.BLACK))

        presenter.changePlayer()
        // 石を配置 ●○x → ●○●
        presenter.onClickPlace(5, 3)
        // クリックした位置に置かれる
        verify(viewMock, times(1)).putStone(Place(5, 3, Stone.BLACK))
        // 挟んだ石が変わる
        verify(viewMock, times(1)).putStone(Place(4, 4, Stone.BLACK))
    }

    @Test
    @JvmName(name = "上下に挟むと色が変わる")
    fun changeStoneAtUpAndUnder() {
        presenter.onCreate(viewMock)
        // 石を配置 x○● → ●○● ※縦
        presenter.onClickPlace(3, 5)
        // クリックした位置に置かれる
        verify(viewMock, times(1)).putStone(Place(3, 5, Stone.BLACK))
        // 挟んだ石が変わる
        verify(viewMock, times(1)).putStone(Place(3, 4, Stone.BLACK))

        presenter.changePlayer()
        // 石を配置 ●○x → ●○● ※縦
        presenter.onClickPlace(4, 2)
        // クリックした位置に置かれる
        verify(viewMock, times(1)).putStone(Place(4, 2, Stone.BLACK))
        // 挟んだ石が変わる
        verify(viewMock, times(1)).putStone(Place(4, 3, Stone.BLACK))
    }
}