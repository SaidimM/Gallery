package com.example.gallery.media.remote

import com.example.gallery.media.remote.album.AlbumResult
import com.example.gallery.media.remote.artist.ArtistResult
import com.example.gallery.media.remote.lyrics.LyricResult
import com.example.gallery.media.remote.music.MusicDetailResult
import com.example.gallery.media.remote.mv.MusicVideoResult
import com.example.gallery.media.remote.search.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.Flow

interface NeteaseApi {

    @Headers(
        "Cookie:NMTID=00OaYeMfDeBCFhdSU4Fjf21c3rx9JQAAAGJY0hCGg; _iuqxldmzr_=32; _ntes_nnid=93e5ccf5593d26a4b35430a0b158ddc5,1690795543635; _ntes_nuid=93e5ccf5593d26a4b35430a0b158ddc5; WEVNSM=1.0.0; WNMCID=zwfegr.1690795546731.01.0; __snaker__id=l0PpnNO5vWvJ7QnK; ntes_utid=tid._.mkr3blc1JfNFQ0UFFVaRw9jPcXCSz%252BFa._.0; WM_TID=CXuMAqK6sStAAEVQRELF0tiOdSDXu3Jm; sDeviceId=YD-ll6Aub7QFMVERlEQQFKRg8iPcSCTiuE0; YD00000558929251%3AWM_TID=PuCmtRfoNb9AEBRAFELRht3LZCWcFvUS; ntes_kaola_ad=1; __csrf=c23751e67850d3fa4f1e365aa8a52c62; WM_NI=R6HZewqrFYsQaUtYsMvQbXywPxODJsleLfybwlFkwWiQE%2BHxgc5tMcgmGuJlmwPrpn4ZSVDvuYyQP3%2BN5h6geZBIRMjQXfDDHL1qE1CBnQ723kaIkQ5H7P5O2CrnQw2FbE8%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6ee94e6609c98ff86c7688aef8fb7d44f869e8e82c4399686a1b8d65d878cacb0d12af0fea7c3b92a92ea88bbe87db3ae8abaf25ba38bbcbaf247b68faeb9fc7d94a881a5c979b0bb8487fc6ab5b48a8cf65dab8f8bcccb74a5bdfb95d069a5b38996c968afa9a093f366f8869db7c77eb787b7b2d339e992fb89b56da7868eaad147aa8f9887c7508c94ff92c83b819ea2b2b63af1b5afaaea5bf798f9b1b845f29dbe8fd47df29aaea6bb37e2a3; gdxidpyhxdE=1BeKCcf%5ClhCUua1PWXKL6Q%2B2a6WyUuGRr5cePwExveQZDnOJyrIKnKUDp2SfU8yXlkxwypZSRBUErXAcLNUbZUfkGlzO4eJyAUoG0DI58j%2BuRtGUTSo5gugH6CX%2BUhWa3KIbbeG4kJ%5C1bUIaT1ePxLfL2Pl%2B%2Bq%5CWwI1K6Rd4fzvB3wAz%3A1692589506935; YD00000558929251%3AWM_NI=%2BdjvEs99AicxHnwcsDzxV9DdAPEDv9%2F3E4%2B6tQQIqgxE1ktzXGlmC%2F%2B18ML9Sl6UK7BfsvF%2B3JcYwIefapcihDrVUAtTIBM0Zomv1TmKHUumv5FStnvFwI%2FStIFcro%2FfMnY%3D; YD00000558929251%3AWM_NIKE=9ca17ae2e6ffcda170e2e6ee98b77bb1a9bdb8e54e958e8ba6d15f868e9e82c4678392f7b9db4d8788e1d6d82af0fea7c3b92ae9bca697c879fbe78f8ae2748cb2b984f250baa89adac77083908ba5f533eded8bd5aa3eb88f9faaf46dbc9bff8cf7729c99a48ec225e9eff8aecf3391b4fab6ca3d82babd97c521f196adbaf643a7baaf91e263ac9cbeb7d84bb88c87a3d03c9ab1a397b460af9588b6f867b6eb8ab4f766b69ab9aaae59b8978a84f165edba838fdc37e2a3; __csrf=b9c70c70ae9377e1756f084a33cae85a; MUSIC_U=00ED5F16A23E5ABC9CAC35432169A8B0040965497D7BB0254D5505D1E981B5B19ABBDDD9C8594763528E142A8533D991BF62DF3A5941C1E4F2F66846A7F508D5B5024D072426959AE822595863726C3C5D5CD71F970D2E54273D04C6020290DE46061D38DC7597793ECC1FFF1DDABC08C919DAEE1AE6E34A41682AB17EEE04CA64DC334426F712E4C10F822B2ED310773437E319A9C59532767D4EF075EEBA8D97FD87923ACAF8BF7C4864B9105F1A8B90FE798975063AB02D0DB2E2E96FDBA2A4D969A25F7BFFECE10E0E1D728D78BDB9EE6A6877100207AFD01A42BA6249C4BBF418F5926EA8713D6D447451E2D6CE00; JSESSIONID-WYYY=8tsqOtNJ6nMI1CCg6v95kW%2FAv4otzc4jOfc4qtMcZ0gJsqngPg0sxmGdzckk0N%2B9O%2Fi9zdYCYznye5no6e7zYTOFVOZH5%5CzB4DmpM%5Cqn%2Fqts%5C%5CoTs0IKRf210zNK85ie0tUzuqPXijnzi%2BdRs87cnsTbTbWHIBZh8R%5CDxZuT7fU4nQ7P%3A1692610437206",
        "Content-type:application/json;charset=UTF-8"
    )
    @GET(value = "/api/search/get/web")
    suspend fun searchMusic(
        @Query("csrf_token=hlpretag") token: String = "",
        @Query("hlposttag") tag: String = "",
        @Query("s") criteria: String,
        @Query("type") type: Int = 1,
        @Query("offset") offset: Int = 0,
        @Query("total") total: Boolean = true,
        @Query("limit") limit: Int = 20
    ): Response<SearchResult>

    @GET(value = "/api/song/lyric")
    suspend fun getLyric(
        @Query("os") os: String = "pc",
        @Query("id") id: String,
        @Query("lv") lv: Int = -1,
        @Query("kv") kv: Int = -1,
        @Query("tv") tv: Int = -1,
    ): Response<LyricResult>

    @GET(value = "/api/mv/detail")
    suspend fun getMv(
        @Query("id") id: String,
        @Query("type") type: String = "mp4"
    ): Response<MusicVideoResult>

    @GET(value = "/api/album/{album_id}")
    suspend fun getAlbumInfo(@Path("album_id") albumId: String): Response<AlbumResult>

    @GET(value = "/api/artist/{artist_id}")
    suspend fun getArtist(@Path("artist_id") artistId: String): Response<ArtistResult>

    //http://music.163.com/api/song/detail/?id=31090820&ids=%5B31090820%5D
    @GET(value = "/api/song/detail/")
    fun getMusicDetail(@Query("id") id: String, @Query("ids") ids: String): Response<MusicDetailResult>
}