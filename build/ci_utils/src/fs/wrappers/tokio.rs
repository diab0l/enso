use crate::prelude::*;

use tokio::fs::File;
use tokio::io::AsyncReadExt;
use tokio_util::io::ReaderStream;



pub fn metadata<P: AsRef<Path>>(path: P) -> BoxFuture<'static, Result<std::fs::Metadata>> {
    let path = path.as_ref().to_owned();
    tokio::fs::metadata(path).anyhow_err().boxed()
}

#[context("Failed to open path for reading: {}", path.as_ref().display())]
pub async fn open(path: impl AsRef<Path>) -> Result<File> {
    File::open(&path).await.anyhow_err()
}

// #[context("Failed to open path for reading: {}", path.as_ref().display())]
pub fn open_stream(path: impl AsRef<Path>) -> BoxFuture<'static, Result<ReaderStream<File>>> {
    let path = path.as_ref().to_owned();
    let file = open(path);
    async move {
        let file = file.await?;
        Ok(ReaderStream::new(file))
    }
    .boxed()
}

#[context("Failed to open path for writing: {}", path.as_ref().display())]
pub async fn create(path: impl AsRef<Path>) -> Result<File> {
    File::create(&path).await.anyhow_err()
}

#[context("Failed to create missing directories no path: {}", path.as_ref().display())]
pub async fn create_dir_all(path: impl AsRef<Path>) -> Result {
    tokio::fs::create_dir_all(&path).await.anyhow_err()
}

#[context("Failed to read the directory: {}", path.as_ref().display())]
pub async fn read_dir(path: impl AsRef<Path>) -> Result<tokio::fs::ReadDir> {
    tokio::fs::read_dir(&path).await.anyhow_err()
}

#[context("Failed to remove directory with the subtree: {}", path.as_ref().display())]
pub async fn remove_dir_all(path: impl AsRef<Path>) -> Result {
    tokio::fs::remove_dir_all(&path).await.anyhow_err()
}

#[context("Failed to write file: {}", path.as_ref().display())]
pub async fn write(path: impl AsRef<Path>, contents: impl AsRef<[u8]>) -> Result {
    tokio::fs::write(&path, &contents).await.anyhow_err()
}

#[context("Failed to read file: {}", path.as_ref().display())]
pub async fn read<P: AsRef<Path>>(path: P) -> Result<Vec<u8>> {
    let mut file = File::open(&path).await?;
    let mut contents = Vec::new();
    file.read_to_end(&mut contents).await?;
    Ok(contents)
}

#[context("Failed to read the file: {}", path.as_ref().display())]
pub async fn read_to_string(path: impl AsRef<Path>) -> Result<String> {
    tokio::fs::read_to_string(&path).await.anyhow_err()
}
