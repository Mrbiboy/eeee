package com.example.e_banking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

// Interface pour définir les appels API
interface ApiService {

    // Requête POST pour la connexion de l'utilisateur
    @FormUrlEncoded
    @POST("login.php")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

     // Requête GET pour récupérer tous les utilisateurs
    @GET("getusers.php")  // Assurez-vous que cette route pointe vers le bon fichier PHP
     fun getUsers(): Call<List<User>>
    // Ajoutez d'autres requêtes API ici si nécessaire

    // Requête GET pour obtenir le solde
    @GET("get_balance.php")
    fun getBalance(
        @Query("username") username: String
    ): Call<BalanceResponse>



    @FormUrlEncoded
    @POST("registerUser.php")
    fun registerUser(
        @Field("nom") nom: String,
        @Field("prenom") prenom: String,
        @Field("cin") cin: String,
        @Field("address") address: String,
        @Field("date_de_naiss") dateNaissance: String,
        @Field("email") email: String,
        @Field("isAdmin") isAdmin: Boolean
    ): Call<ResponseBody>


    @POST("withdraw.php")
    @FormUrlEncoded
    fun withdraw(
        @Field("username") username: String,
        @Field("amount") amount: Double
    ): Call<BalanceResponse>
    // Define the getUserDetails method
    @GET("getusers.php")  // Assuming your API is accessible at this endpoint
    fun getUserDetails(
        @Query("username") username: String  // The username is passed as a query parameter
    ): Call<User>

    @FormUrlEncoded
    @POST("deleteUser.php")
    fun deleteUser(
        @Field("username") username: String
    ): Call<Void>

    @GET("api/getUserDetails.php")
    fun getUserDetails(@Query("userId") userId: Int): Call<User>

    @GET("api/getBalance.php")
    fun getBalance(@Query("userId") userId: Int): Call<BalanceResponse>

    @POST("api/withdraw.php")
    @FormUrlEncoded
    fun withdraw(@Field("userId") userId: Int, @Field("amount") amount: Double): Call<BalanceResponse>
    @GET("getUserByUsername.php")
    fun getUserByUsername(@Query("username") username: String): Call<Useer>

    @POST("performTransfer.php")
    fun performTransfer(
        @Query("beneficiaryId") beneficiaryId: Int,
        @Query("senderId") senderId: Int,
        @Query("amount") amount: Double
    ): Call<TransferResponse>
    @POST("verifyECode.php")
    fun verifyECode(@Query("userId") userId: Int, @Query("e_code") eCode: String): Call<LoginResponse>

    @POST("updateAccounts.php")
    fun updateAccounts(
        @Field("senderId") senderId: Int,
        @Field("beneficiaryId") beneficiaryId: Int,
        @Field("amount") amount: Double
    ): Call<LoginResponse>
}
