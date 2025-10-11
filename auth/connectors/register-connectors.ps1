# Путь к папке с JSON-коннекторами
$connectorsPath = "D:\JAVA\simple-taxi\auth\connectors"

# URL Debezium Connect REST API
$connectUrl = "http://localhost:8083/connectors"

# Получаем все JSON-файлы в папке
$files = Get-ChildItem "$connectorsPath\*.json"

foreach ($file in $files) {
    try {
        Write-Host ("Регистрация коннектора из файла: " + $file.Name)
        $response = Invoke-RestMethod -Uri $connectUrl `
                                      -Method Post `
                                      -ContentType "application/json" `
                                      -InFile $file.FullName
        Write-Host ("Успех: " + $response.name)
    }
    catch {
        Write-Host ("Ошибка при регистрации коннектора " + $file.Name + ": " + $_.Exception.Message) -ForegroundColor Red
    }
}
