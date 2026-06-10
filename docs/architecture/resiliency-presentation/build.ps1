$env:Path = "C:\Users\ambarana\.local\bin;$env:Path"
Set-Location "c:\Users\ambarana\.vscode\extensions\ise-hve-essentials.hve-core-all-3.2.2\.github\skills\experimental\powerpoint"
.\scripts\Invoke-PptxPipeline.ps1 -Action Build -ContentDir "c:\Users\ambarana\ecommerce-platform\docs\architecture\resiliency-presentation\content" -StylePath "c:\Users\ambarana\ecommerce-platform\docs\architecture\resiliency-presentation\content\global\style.yaml" -OutputPath "c:\Users\ambarana\ecommerce-platform\docs\architecture\resiliency-presentation\slide-deck\resiliency-research-skill.pptx" 2>&1
Write-Host "Build exit code: $LASTEXITCODE"
